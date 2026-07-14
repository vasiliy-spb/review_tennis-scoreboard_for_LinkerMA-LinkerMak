package tennis.score.board.model.matchstate.game.tiebreak;

import tennis.score.board.model.matchstate.WinnerSide;
import tennis.score.board.model.matchstate.game.GameResult;
import tennis.score.board.model.matchstate.game.GameScore;
import tennis.score.board.model.matchstate.game.GameScoreSnapshot;

import java.util.Optional;

public class TieBreakGameScore implements GameScore {

    // Префикс TIE_BREAK можно удалить — этот контекст понятен из названия класса
    private static final int TIE_BREAK_MIN_POINTS_TO_WIN = 7;

    private static final int REQUIRED_LEAD = 2;

    private int pointsPlayer1 = 0;
    private int pointsPlayer2 = 0;

    @Override
    public GameResult pointWonBy(WinnerSide winnerSide) {
        // TODO: Нет проверки на то, что тай-брейк не завершён.
            // Попытка начислить очко в уже завершённом тай-брейке — это не нормальная ситуация и
            // должна приводить к исключению.

        switch (winnerSide) {
            case PLAYER_1 -> pointsPlayer1++;
            case PLAYER_2 -> pointsPlayer2++;
        }

        if(tieBreakMinPointsCondition() && tieBreakLeadCondition()){
            return GameResult.FINISHED;
        }
        return GameResult.CONTINUES;
    }

    // В java методы принято называть глаголами, а возвращающие boolean — в стиле вопросительного предложения:
        // например, isTieBreakMinPointsCondition
    private boolean tieBreakMinPointsCondition() {
        return pointsPlayer1 >= TIE_BREAK_MIN_POINTS_TO_WIN
                || pointsPlayer2 >= TIE_BREAK_MIN_POINTS_TO_WIN;
    }

    // В java методы принято называть глаголами, а возвращающие boolean — в стиле вопросительного предложения:
        // например, isTieBreakLeadCondition
    private boolean tieBreakLeadCondition() {
        return pointsLeadOfPlayer1() >= REQUIRED_LEAD || pointsLeadOfPlayer2() >= REQUIRED_LEAD;
    }

    // В java принято называть методы глаголами: например, getPointsLeadOfPlayer1
    private int pointsLeadOfPlayer1() {
        return pointsPlayer1 - pointsPlayer2;
    }

    // В java принято называть методы глаголами: например, getPointsLeadOfPlayer2
    private int pointsLeadOfPlayer2() {
        return pointsPlayer2 - pointsPlayer1;
    }

    @Override
    public Optional<WinnerSide> getWinner() {
        if (tieBreakMinPointsCondition() && tieBreakLeadCondition()) {
            return Optional.of(pointsPlayer1 > pointsPlayer2
                    ? WinnerSide.PLAYER_1 : WinnerSide.PLAYER_2);
        }

        // Когда из блока if происходит return, то следующую ветку можно писать без else.
        // Сообщения в исключениях принято писать на английском языке.
        else throw new IllegalStateException("Не удалось опредедить победителя тай-брейка");
    }

    @Override
    public GameScoreSnapshot snapshot() {
        return new GameScoreSnapshot(
                String.valueOf(pointsPlayer1),
                String.valueOf(pointsPlayer2)
        );
    }
}
