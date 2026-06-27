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

    private int player1Games = 0;
    private int player2Games = 0;

    private GameScore gameScore = new RegularGameScore();

    private static final int REQUIRED_LEAD = 2;

    private static final int GAMES_TO_WIN_REGULAR_SET = 6;

    private static final int GAMES_TO_WIN_SET_FOR_WINNER = 7;
    private static final int GAMES_TO_WIN_EXTENDED_SET_FOR_LOSER = 5;

    private static final int TIE_BREAK_START_GAMES = 6;
    private static final int GAMES_TO_WIN_TIE_BREAK_FOR_LOSER = 6;

    public Optional<WinnerSide> pointWonBy(WinnerSide winnerSide) {
        GameResult gameResult = gameScore.pointWonBy(winnerSide);

        switch(gameResult) {
            case GameResult.FINISHED -> handleFinishedResult();
            case GameResult.TRANSITION_TO_DEUCE -> handleTransitionToDeuceResult();
            case GameResult.CONTINUES -> {}
        }

        return tryFinishSet();
    }

    private void handleFinishedResult() {
        Optional<WinnerSide> winner = gameScore.getWinner();

        if(winner.isEmpty()) {
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

    private void handleTransitionToDeuceResult() {
        this.gameScore = new DeuceGameScore();
    }

    private Optional<WinnerSide> tryFinishSet() {
        if(isExtendedSetPhase()) {
            return tryFinishExtendedSetOrStartTieBreak();
        }

        return tryFinishRegularSet();
    }

    private boolean isExtendedSetPhase() {
        return player1Games > 4 && player2Games > 4;
    }

    private Optional<WinnerSide> tryFinishExtendedSetOrStartTieBreak() {
        if (player1Games == GAMES_TO_WIN_SET_FOR_WINNER) {
            if (player2Games == GAMES_TO_WIN_EXTENDED_SET_FOR_LOSER || player2Games == GAMES_TO_WIN_TIE_BREAK_FOR_LOSER) {
                return Optional.of(WinnerSide.PLAYER_1);
            }
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
