package tennis.score.board.model.matchstate.match;

public record MatchScoreSnapshot(

        // Класс называется MatchScoreSnapshot, но хранит снимок счёта и в сете и в гейме (а также в тай-брейке).
            // Для счёта в гейме и сете уже есть специальные GameScoreSnapshot и SetScoreSnapshot,
            // поэтому здесь можно хранить только счёт матча или использовать GameScoreSnapshot и SetScoreSnapshot.

        int player1Sets,
        int player2Sets,
        int player1Games,
        int player2Games,
        String player1DisplayPoints,
        String player2DisplayPoints,
        boolean tieBreak
) {
}
