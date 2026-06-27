package tennis.score.board.model.matchstate.set;

public record SetScoreSnapshot(
        int player1Games,
        int player2Games,
        String player1DisplayPoints,
        String player2DisplayPoints,
        boolean tieBreak
) {
}
