package tennis.score.board.model.matchstate.match;

public record MatchScoreSnapshot(
        int player1Sets,
        int player2Sets,
        int player1Games,
        int player2Games,
        String player1DisplayPoints,
        String player2DisplayPoints,
        boolean tieBreak
) {
}
