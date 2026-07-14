package tennis.score.board.model.matchstate;

public record ScoreboardSnapshot(

        // Для счёта в гейме, сете и матче уже есть специальные GameScoreSnapshot, SetScoreSnapshot, MatchScoreSnapshot
            // поэтому здесь можно использовать эти объекты.

        Long player1Id,
        Long player2Id,
        String player1Name,
        String player2Name,
        int player1Sets,
        int player2Sets,
        int player1Games,
        int player2Games,
        String player1Points,
        String player2Points,
        boolean tieBreak
) {
}
