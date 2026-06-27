package tennis.score.board.model.matchstate.match;

import tennis.score.board.model.matchstate.WinnerSide;
import tennis.score.board.model.matchstate.set.SetScore;
import tennis.score.board.model.matchstate.set.SetScoreSnapshot;

import java.util.Optional;

public class MatchScore {

    private int player1Sets = 0;
    private int player2Sets = 0;

    private SetScore setScore = new SetScore();

    private final static int SETS_TO_WIN = 2;

    public void pointWonBy(WinnerSide winnerSide) {
        Optional<WinnerSide> optionalWinnerSide = setScore.pointWonBy(winnerSide);

        optionalWinnerSide.ifPresent(this::handleFinishSet);
    }

    private void handleFinishSet(WinnerSide winnerSide) {
        switch(winnerSide) {
            case WinnerSide.PLAYER_1 -> player1Sets++;
            case WinnerSide.PLAYER_2 -> player2Sets++;
        }

        if(!isOver()) {
            setScore = new SetScore();
        }
    }

    public boolean isOver() {
        return player1Sets == SETS_TO_WIN || player2Sets == SETS_TO_WIN;
    }

    public WinnerSide getWinnerSide() {
        if(!isOver()) {
            throw new IllegalStateException("Попытка получить победителя, когда матч еще не закончен");
        }

        return (player1Sets == SETS_TO_WIN)
                ? WinnerSide.PLAYER_1
                : WinnerSide.PLAYER_2;
    }

    public MatchScoreSnapshot snapshot() {
        SetScoreSnapshot setScoreSnapshot = setScore.snapshot();

        return new MatchScoreSnapshot(
                player1Sets,
                player2Sets,
                setScoreSnapshot.player1Games(),
                setScoreSnapshot.player2Games(),
                setScoreSnapshot.player1DisplayPoints(),
                setScoreSnapshot.player2DisplayPoints(),
                setScoreSnapshot.tieBreak()
        );
    }
}
