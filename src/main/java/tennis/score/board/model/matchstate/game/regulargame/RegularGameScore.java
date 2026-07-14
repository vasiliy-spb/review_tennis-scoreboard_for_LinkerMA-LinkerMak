package tennis.score.board.model.matchstate.game.regulargame;

import tennis.score.board.model.matchstate.WinnerSide;
import tennis.score.board.model.matchstate.game.GameResult;
import tennis.score.board.model.matchstate.game.GameScore;
import tennis.score.board.model.matchstate.game.GameScoreSnapshot;

import java.util.Optional;

public class RegularGameScore implements GameScore {

    // TODO: "Кодирование" счёта.
        // Использование поля int rank из Points лишает смысла само существование перечисления.
        // Логика обработки счёта в гейме должна полагаться на значения констант enum,
        // а не на их условный порядковый номер.

    private Points player1Points = Points.LOVE;
    private Points player2Points = Points.LOVE;

    // Константы объявляются первыми (пишутся в самом верху) в классе.
    private static final int REQUIRED_LEAD = 2;

    @Override
    public GameResult pointWonBy(WinnerSide winnerSide) {
        // TODO: Нет проверки на то, что гейм не завершён.
            // Попытка начислить очко в уже завершённом гейме — это не нормальная ситуация и
            // должна приводить к исключению.

        switch(winnerSide) {
            case PLAYER_1 -> player1Points = player1Points.next();
            case PLAYER_2 -> player2Points = player2Points.next();
        }
        return gameResult();
    }

    // В java принято называть методы глаголами: например, determineGameResult
    private GameResult gameResult() {
        if(isFinished()) {
            return GameResult.FINISHED;
        }

        // Когда из блока if происходит return, то следующую ветку можно писать без else.
        else if(isDeuce()) {
            return GameResult.TRANSITION_TO_DEUCE;
        }

        return GameResult.CONTINUES;
    }

    private boolean isFinished() {
        return getWinner().isPresent();
    }

    public boolean isDeuce() {
        return player1Points == Points.FORTY && player2Points == Points.FORTY;
    }

    @Override
    public Optional<WinnerSide> getWinner() {
        if(player1Points == Points.WIN_POINT && pointsLeadOfPlayer1() >= REQUIRED_LEAD) {
            return Optional.of(WinnerSide.PLAYER_1);
        }

        // Когда из блока if происходит return, то следующую ветку можно писать без else.
        else if(player2Points == Points.WIN_POINT && pointsLeadOfPlayer2() >= REQUIRED_LEAD) {
            return Optional.of(WinnerSide.PLAYER_2);
        }
        return Optional.empty();
    }

    private int pointsLeadOfPlayer1() {
        return player1Points.rank() - player2Points.rank();
    }

    private int pointsLeadOfPlayer2() {
        return player2Points.rank() - player1Points.rank();
    }

    @Override
    public GameScoreSnapshot snapshot() {
        return new GameScoreSnapshot(
                player1Points.displayValue(),
                player2Points.displayValue());
    }
}
