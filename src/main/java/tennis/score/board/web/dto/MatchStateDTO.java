package tennis.score.board.web.dto;

public record MatchStateDTO(

        // Больше подошло бы название MatchScoreDto

        // TODO: Нет полей для счёта в тай-брейке

        // Сейчас все поля, относящиеся к счёту игрока, дублируются для первого и второго игрока.
            // Такой подход делает классы большими и громоздкими и нарушает принцип DRY (Don't Repeat Yourself).
            // Также, чтобы добавить счёт в тай-брейке для каждого игрока, понадобится добавить два поля.
            // Можно ввести DTO для счёта одного игрока и хранить два таких DTO внутри MatchStateDTO.

        Long player1Id,
        String player1Name,
        String player1Points,
        int player1Games,
        int player1Sets,

        Long player2Id,
        String player2Name,
        String player2Points,
        int player2Games,
        int player2Sets,

        boolean tieBreak
) {
}
