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

    // TODO: Нет интерфейса для этого класса. (см. файл "service.md" в этом же пакете)

    // TODO: Класс отвечает за создание и хранение объекта текущего матча (доменной модели).
        // При этом он способствует смешению слоёв — работает с JPA Entity и передаёт их в доменную модель.
        // (см. файл "separation-of-concerns-principle.md" в этом же пакете)

    // TODO: Класс нарушает Принцип единой ответственности (SRP).
        // Он выполняет несколько разных задач:
            // - управляет хранилищем текущих матчей
            // - занимается преобразованием доменных моделей в DTO (хоть и через маппер)
            // - занимается валидацией игроков
            // - управляет запланированными задачами
        // Как исправить:
            // Ответственности можно было бы разделить на несколько более сфокусированных классов

    // Методы handleMatchOngoing и handleMatchOverBeforeUpdate почти полностью идентичны (кроме статуса).
        // Можно оставить только один метод и принимать статус в качестве аргумента

    private final Map<UUID, MatchState> matches = new ConcurrentHashMap<>();
    private final MatchStateMapper matchStateMapper;

    private final MatchService matchService;

    private final Map<UUID, Long> removeAt = new ConcurrentHashMap<>();

    // Константы объявляются первыми (пишутся в самом верху) в классе.
    private final static long FINISHED_MATCH_GRACE_PERIOD_MILLIS = 10_000L;

    // Если у класса есть ровно один конструктор, Spring автоматически использует его для внедрения зависимостей —
        // даже без @Autowired. Можно удалить конструктор и поставить над классом @RequiredArgsConstructor
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

        // Можно удалять матч сразу при завершении, а запланированное удаление оставить только для "заброшенных" матчей
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

            // Сообщения в исключениях принято писать на английском языке.
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

            // Сообщения в исключениях принято писать на английском языке.
            throw new BadRequestException("Игроки обязательны");
        }

        // Проверка на null обоих имён уже выполняется в первом if — здесь можно её не дублировать
        if (player1.getId() != null && player2.getId() != null
                && Objects.equals(player1.getId(), player2.getId())) {

            // Сообщения в исключениях принято писать на английском языке.
            throw new BadRequestException("Нельзя создать матч игрока с самим собой");
        }

        // Проверка на null обоих имён уже выполняется в первом if — здесь можно её не дублировать
        if (player1.getName() != null && player2.getName() != null
                && player1.getName().equalsIgnoreCase(player2.getName())) {

            // Сообщения в исключениях принято писать на английском языке.
            throw new BadRequestException("Нельзя создать матч игрока с самим собой");
        }
    }

    // Отсутствует явное указание модификатора доступа
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
