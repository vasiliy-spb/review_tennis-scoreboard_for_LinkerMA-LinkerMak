package tennis.score.board.model.matchstate.game.deuce;

import tennis.score.board.model.matchstate.WinnerSide;
import tennis.score.board.model.matchstate.game.GameResult;
import tennis.score.board.model.matchstate.game.GameScore;
import tennis.score.board.model.matchstate.game.GameScoreSnapshot;

import java.util.Optional;

public class DeuceGameScore implements GameScore {

    // TODO: В существовании этого класса нет необходимости — он забирает часть ответственности у RegularGameScore,
        // что размазывает цельную логику по нескольким классам.

    // Логика методов handleAdvantage_P1 и handleAdvantage_P2 полностью дублируется для разных игроков. Стоит придумать, как избавиться от дублирования.


    // Отсутствует явное указание модификатора доступа
    DeucePoints pointsState = DeucePoints.DEUCE;

    @Override
    public GameResult pointWonBy(WinnerSide winnerSide) {
        return switch(pointsState) {
            case DeucePoints.DEUCE -> handleDueceState(winnerSide);
            case DeucePoints.ADVANTAGE_P1 -> handleAdvantage_P1(winnerSide);
            case DeucePoints.ADVANTAGE_P2 -> handleAdvantage_P2(winnerSide);
        };
    }

    // Опечатка handleDueceState —> handleDeuceState
    private GameResult handleDueceState(WinnerSide winnerSide) {
        if(winnerSide == WinnerSide.PLAYER_1) {
            pointsState = DeucePoints.ADVANTAGE_P1;
        }

        if(winnerSide == WinnerSide.PLAYER_2) {
            pointsState = DeucePoints.ADVANTAGE_P2;
        }

        return GameResult.CONTINUES;
    }

    // В java принято именовать методы в стиле camelCase: handleAdvantageP1
    private GameResult handleAdvantage_P1(WinnerSide winnerSide) {
        if(winnerSide == WinnerSide.PLAYER_1) {
            return GameResult.FINISHED;
        }

        // Когда из блока if происходит return, то следующий код можно писать без else.
        else {
            pointsState = DeucePoints.DEUCE;
            return GameResult.CONTINUES;
        }
    }

    // В java принято именовать методы в стиле camelCase: handleAdvantageP2
    private GameResult handleAdvantage_P2(WinnerSide winnerSide) {
        if(winnerSide == WinnerSide.PLAYER_2) {
            return GameResult.FINISHED;
        }

        // Когда из блока if происходит return, то следующий код можно писать без else.
        else {
            pointsState = DeucePoints.DEUCE;
            return GameResult.CONTINUES;
        }
    }

    @Override
    public Optional<WinnerSide> getWinner() {
        return Optional.of(switch(pointsState) {
            case ADVANTAGE_P1 -> WinnerSide.PLAYER_1;
            case ADVANTAGE_P2 -> WinnerSide.PLAYER_2;

            // Сообщения в исключениях принято писать на английском языке.
            case DEUCE -> throw new IllegalStateException("Не удалось определить победителя деюса");
        });
    }

    @Override
    public GameScoreSnapshot snapshot() {
        return new GameScoreSnapshot(
                pointsState.getDisplayValuePlayer1(),
                pointsState.getDisplayValuePlayer2()
        );
    }
}
