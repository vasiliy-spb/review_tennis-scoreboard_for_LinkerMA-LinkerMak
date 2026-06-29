package tennis.score.board.model.matchstate;

import lombok.Getter;
import tennis.score.board.model.entity.Player;
import tennis.score.board.model.matchstate.match.MatchScore;
import tennis.score.board.model.matchstate.match.MatchScoreSnapshot;

public class MatchState {

    @Getter
    private final Player player1;
    @Getter
    private final Player player2;

    private final MatchScore matchScore;

    public MatchState(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.matchScore = new MatchScore();
    }

    public void updateScore(WinnerSide winnerSide) {
        matchScore.pointWonBy(winnerSide);
    }

    public boolean isOver() {
        return matchScore.isOver();
    }

    public Player getMatchWinner() {
        return switch(matchScore.getWinnerSide()) {
            case PLAYER_1 -> player1;
            case PLAYER_2 -> player2;
        };
    }

    public ScoreboardSnapshot snapshot() {
        MatchScoreSnapshot matchScoreSnapshot = matchScore.snapshot();

        return new ScoreboardSnapshot(
                player1.getId(),
                player2.getId(),
                player1.getName(),
                player2.getName(),
                matchScoreSnapshot.player1Sets(),
                matchScoreSnapshot.player2Sets(),
                matchScoreSnapshot.player1Games(),
                matchScoreSnapshot.player2Games(),
                matchScoreSnapshot.player1DisplayPoints(),
                matchScoreSnapshot.player2DisplayPoints(),
                matchScoreSnapshot.tieBreak()
        );
    }

}
