package tennis.score.board.model.matchstate.set;

import tennis.score.board.model.matchstate.WinnerSide;
import tennis.score.board.model.matchstate.game.GameResult;
import tennis.score.board.model.matchstate.game.GameScore;
import tennis.score.board.model.matchstate.game.GameScoreSnapshot;
import tennis.score.board.model.matchstate.game.regulargame.RegularGameScore;
import tennis.score.board.model.matchstate.game.deuce.DeuceGameScore;
import tennis.score.board.model.matchstate.game.tiebreak.TieBreakGameScore;

import java.util.Optional;


public class SetScore {

    // Константы объявляются первыми (пишутся в самом верху) в классе.

    // Логика класса избыточно запутана. Более простой вариант (в методе pointWonBy):
        // 1. начислить в текущем гейме/тай-брейке очко победителю
        // 2. Если гейм/тай-брейк завершён — начислить очко в сете

    private int player1Games = 0;
    private int player2Games = 0;

    private GameScore gameScore = new RegularGameScore();

    private static final int REQUIRED_LEAD = 2;

    private static final int GAMES_TO_WIN_REGULAR_SET = 6;

    private static final int GAMES_TO_WIN_SET_FOR_WINNER = 7;

    // Сбивающее с толку название: "геймы для победы в расширенном сете для проигравшего" —
        // не понятно, что за геймы для победы для проигравшего
    private static final int GAMES_TO_WIN_EXTENDED_SET_FOR_LOSER = 5;

    private static final int TIE_BREAK_START_GAMES = 6;

    // Сбивающее с толку название: "геймы для победы в тай-брейке для проигравшего" —
        // не понятно, что за геймы для победы для проигравшего
    private static final int GAMES_TO_WIN_TIE_BREAK_FOR_LOSER = 6;

    // TODO: Метод нарушает Принцип разделения команд и запросов (см. файл "cqs-principle.md" в этом же пакете)
    public Optional<WinnerSide> pointWonBy(WinnerSide winnerSide) {
        // TODO: Нет проверки на то, что сет не завершён.
            // Попытка начислить очко в уже завершённом сете — это не нормальная ситуация и
            // должна приводить к исключению.

        GameResult gameResult = gameScore.pointWonBy(winnerSide);

        switch(gameResult) {
            case GameResult.FINISHED -> handleFinishedResult();

            // TODO: Класс отвечающий за очки в сете не должен знать и управлять логикой гейма
                // (включать режим больше-меньше)
            case GameResult.TRANSITION_TO_DEUCE -> handleTransitionToDeuceResult();
            case GameResult.CONTINUES -> {}
        }

        return tryFinishSet();
    }

    private void handleFinishedResult() {
        Optional<WinnerSide> winner = gameScore.getWinner();

        if(winner.isEmpty()) {

            // Сообщения в исключениях принято писать на английском языке.
            throw new IllegalStateException("Не удалось получить победителя гейма");
        }

        switch(winner.get()) {
            case WinnerSide.PLAYER_1 -> player1Games++;
            case WinnerSide.PLAYER_2 -> player2Games++;
        }

        if(!shouldStartTieBreak()) {
            this.gameScore = new RegularGameScore();
        }
    }

    // Класс отвечающий за очки в сете не должен знать и управлять логикой гейма (больше-меньше)
    private void handleTransitionToDeuceResult() {
        this.gameScore = new DeuceGameScore();
    }

    // Метод нарушает Принцип разделения команд и запросов // (см. файл "cqs-principle.md" в этом же пакете)
    private Optional<WinnerSide> tryFinishSet() {
        if(isExtendedSetPhase()) {
            return tryFinishExtendedSetOrStartTieBreak();
        }

        return tryFinishRegularSet();
    }

    private boolean isExtendedSetPhase() {

        // "Магические числа" лучше вынести в именованные константы.
            // Именованная константа делает код более читаемым и понятным.
        return player1Games > 4 && player2Games > 4;
    }

    // TODO: Метод нарушает Принцип разделения команд и запросов (см. файл "cqs-principle.md" в этом же пакете)
    // TODO: Метод нарушает Принцип единой ответственности на уровне метода:
        // - проверяет условия для победы и определяет победителя
        // - проверяет условия для старта тай-брейка и начинает тай-брейк
    private Optional<WinnerSide> tryFinishExtendedSetOrStartTieBreak() {

        // Вместо вложенные if лучше использовать составные условия через &&
        // Сложные или составные условия из if лучше выносить в отдельный метод с понятным названием
        if (player1Games == GAMES_TO_WIN_SET_FOR_WINNER) {
            if (player2Games == GAMES_TO_WIN_EXTENDED_SET_FOR_LOSER || player2Games == GAMES_TO_WIN_TIE_BREAK_FOR_LOSER) {
                return Optional.of(WinnerSide.PLAYER_1);
            }

        // Вместо вложенные if лучше использовать составные условия через &&
        // Сложные или составные условия из if лучше выносить в отдельный метод с понятным названием
        } else if (player2Games == GAMES_TO_WIN_SET_FOR_WINNER) {
            if (player1Games == GAMES_TO_WIN_EXTENDED_SET_FOR_LOSER || player1Games == GAMES_TO_WIN_TIE_BREAK_FOR_LOSER) {
                return Optional.of(WinnerSide.PLAYER_2);
            }
        } else if (shouldStartTieBreak()) {
            this.gameScore = new TieBreakGameScore();
        }
        return Optional.empty();
    }

    private boolean shouldStartTieBreak() {
        return player1Games == TIE_BREAK_START_GAMES
                && player2Games == TIE_BREAK_START_GAMES
                && !(gameScore instanceof TieBreakGameScore);
    }

    // Более точным было бы название isTieBreak
    private boolean isTieBreakInProgress() {
        return gameScore instanceof TieBreakGameScore;
    }

    private Optional<WinnerSide> tryFinishRegularSet() {
        if(player1Games == GAMES_TO_WIN_REGULAR_SET
                && gamesLeadOfPlayer1() >= REQUIRED_LEAD) {
            return Optional.of(WinnerSide.PLAYER_1);
        }
        else if(player2Games == GAMES_TO_WIN_REGULAR_SET
                && gamesLeadOfPlayer2() >= REQUIRED_LEAD) {
            return Optional.of(WinnerSide.PLAYER_2);
        }
        return Optional.empty();
    }

    private int gamesLeadOfPlayer1() {
        return player1Games - player2Games;
    }

    private int gamesLeadOfPlayer2() {
        return player2Games - player1Games;
    }

    public SetScoreSnapshot snapshot(){
        GameScoreSnapshot gameScoreSnapshot = gameScore.snapshot();

        return new SetScoreSnapshot(
                player1Games,
                player2Games,
                gameScoreSnapshot.player1Points(),
                gameScoreSnapshot.player2points(),
                isTieBreakInProgress()
        );
    }
}
