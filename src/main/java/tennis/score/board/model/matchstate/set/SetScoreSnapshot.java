package tennis.score.board.model.matchstate.set;

public record SetScoreSnapshot(

        // Класс называется SetScoreSnapshot, но хранит снимок счёта и в сете и в гейме (а также в тай-брейке).
            // Для счёта в гейме уже есть специальный GameScoreSnapshot,
            // поэтому здесь можно хранить только счёт сета или использовать GameScoreSnapshot.

        int player1Games,
        int player2Games,
        String player1DisplayPoints,
        String player2DisplayPoints,
        boolean tieBreak
) {
}
