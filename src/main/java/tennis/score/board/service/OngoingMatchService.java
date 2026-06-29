package tennis.score.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tennis.score.board.exception.BadRequestException;
import tennis.score.board.exception.EntityNotFoundException;
import tennis.score.board.exception.PlayerNotInMatchException;
import tennis.score.board.model.entity.Match;
import tennis.score.board.model.entity.Player;
import tennis.score.board.model.matchstate.MatchState;
import tennis.score.board.model.matchstate.WinnerSide;
import tennis.score.board.service.updateresult.MatchStatus;
import tennis.score.board.service.updateresult.UpdateMatchResult;
import tennis.score.board.web.dto.MatchStateDTO;
import tennis.score.board.web.mapper.MatchStateMapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static tennis.score.board.model.matchstate.WinnerSide.PLAYER_1;
import static tennis.score.board.model.matchstate.WinnerSide.PLAYER_2;

@Service
public class OngoingMatchService {

    private final Map<UUID, MatchState> matches = new ConcurrentHashMap<>();
    private final MatchStateMapper matchStateMapper;

    private final MatchService matchService;

    private final Map<UUID, Long> removeAt = new ConcurrentHashMap<>();
    private final static long FINISHED_MATCH_GRACE_PERIOD_MILLIS = 10_000L;

    @Autowired
    public OngoingMatchService(MatchStateMapper matchStateMapper, MatchService matchService) {
        this.matchStateMapper = matchStateMapper;
        this.matchService = matchService;
    }

    public UUID createMatch(Player player1, Player player2) {
        validatePlayersAreDifferent(player1, player2);
        UUID uuid = UUID.randomUUID();
        matches.put(uuid, new MatchState(player1, player2));
        return uuid;
    }

    public MatchStateDTO getMatchByUUID(UUID uuid) {
        return matchStateMapper.toMatchStateDTO(getExistingMatch(uuid).snapshot());
    }

    public UpdateMatchResult updateMatch(UUID uuid, Long winnerId) {
        MatchState match = getExistingMatch(uuid);
        synchronized (match) {
            validatePlayerBelongsToMatch(match, winnerId);

            if (match.isOver()) {
                return handleMatchOverBeforeUpdate(match);
            }

            WinnerSide winnerSide = (Objects.equals(winnerId, match.getPlayer1().getId()))
                    ? PLAYER_1
                    : PLAYER_2;
            match.updateScore(winnerSide);

            if (match.isOver()) {
                return handleMatchOverAfterUpdate(match, uuid);
            }

            return handleMatchOngoing(match);
        }
    }

    private UpdateMatchResult handleMatchOngoing(MatchState match){
        return new UpdateMatchResult(MatchStatus.ONGOING,
                matchStateMapper.toMatchStateDTO(match.snapshot()));
    }

    private UpdateMatchResult handleMatchOverBeforeUpdate(MatchState match) {
        return new UpdateMatchResult(
                MatchStatus.FINISHED,
                matchStateMapper.toMatchStateDTO(match.snapshot())
        );
    }

    private UpdateMatchResult handleMatchOverAfterUpdate(MatchState match, UUID uuid) {
        Player winner = match.getMatchWinner();

        removeAt.put(uuid, System.currentTimeMillis() + FINISHED_MATCH_GRACE_PERIOD_MILLIS);

        matchService.saveMatch(new Match(
                match.getPlayer1(),
                match.getPlayer2(),
                winner
        ));

        return new UpdateMatchResult(MatchStatus.FINISHED, matchStateMapper.toMatchStateDTO(match.snapshot()));
    }

    private MatchState getExistingMatch(UUID uuid) {
        MatchState matchState = matches.get(uuid);
        if(matchState == null) {
            throw new EntityNotFoundException("Сущность матча по id = " + uuid + " не найдена");
        }

        return matchState;
    }

    private void validatePlayerBelongsToMatch(MatchState match,Long id) {
        if(!Objects.equals(id, match.getPlayer1().getId())
                && !Objects.equals(id, match.getPlayer2().getId())) {
            throw new PlayerNotInMatchException(id);
        }
    }


    private void validatePlayersAreDifferent(Player player1, Player player2) {
        if (player1 == null || player2 == null) {
            throw new BadRequestException("Игроки обязательны");
        }

        if (player1.getId() != null && player2.getId() != null
                && Objects.equals(player1.getId(), player2.getId())) {
            throw new BadRequestException("Нельзя создать матч игрока с самим собой");
        }

        if (player1.getName() != null && player2.getName() != null
                && player1.getName().equalsIgnoreCase(player2.getName())) {
            throw new BadRequestException("Нельзя создать матч игрока с самим собой");
        }
    }

    @Scheduled(fixedDelay = 1000)
    void cleanupFinishedMatches() {
        for(Map.Entry<UUID, Long> entry : removeAt.entrySet()) {
            if(System.currentTimeMillis() >= entry.getValue()) {
                matches.remove(entry.getKey());
                removeAt.remove(entry.getKey());
            }
        }
    }
}
