package tennis.score.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tennis.score.board.exception.EntityNotFoundException;
import tennis.score.board.model.entity.Match;
import tennis.score.board.model.entity.Player;
import tennis.score.board.model.match.MatchState;
import tennis.score.board.service.updateresult.MatchStatus;
import tennis.score.board.service.updateresult.UpdateMatchResult;
import tennis.score.board.web.dto.MatchStateDTO;
import tennis.score.board.web.mapper.MatchStateMapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OngoingMatchService {

    private final Map<UUID, MatchState> matches = new ConcurrentHashMap<>();
    private final MatchStateMapper matchStateMapper;

    private final MatchService matchService;

    @Autowired
    public OngoingMatchService(MatchStateMapper matchStateMapper, MatchService matchService) {
        this.matchStateMapper = matchStateMapper;
        this.matchService = matchService;
    }

    public UUID createMatch(Player player1, Player player2) {
        UUID uuid = UUID.randomUUID();
        matches.put(uuid, new MatchState(player1, player2));
        return uuid;
    }

    public MatchStateDTO getMatchByUUID(UUID uuid) {
        validateMatchExistence(uuid);
        return matchStateMapper.toMatchStateDTO(matches.get(uuid));
    }

    public UpdateMatchResult updateMatch(UUID uuid, Long winnerId) {
        validateMatchExistence(uuid);
        MatchState match = matches.get(uuid);
        match.validatePlayerBelongsToMatch(winnerId);

        match.updateScore(winnerId);
        MatchStateDTO matchStateDTO = matchStateMapper.toMatchStateDTO(match);

        if(match.isOver()) {
            handleMatchOver(match, uuid);
            return new UpdateMatchResult(MatchStatus.FINISHED, matchStateDTO);
        }

        return new UpdateMatchResult(MatchStatus.ONGOING, matchStateDTO);
    }

    private void handleMatchOver(MatchState match, UUID uuid) {
        Optional<Player> winner = match.getWinner();

        if(winner.isEmpty()) {
            throw new IllegalStateException("Не удалоь определить победителя завершенного матча");
        }

        matches.remove(uuid);

        matchService.saveMatch(new Match(
                match.getPlayer1(),
                match.getPlayer2(),
                winner.get()
        ));
    }

    public void validateMatchExistence(UUID uuid) {
        if(!matches.containsKey(uuid)) {
            throw new EntityNotFoundException("Сущность матча по id = " + uuid + " не найдена");
        }
    }

}
