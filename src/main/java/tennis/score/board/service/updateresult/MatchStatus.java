package tennis.score.board.service.updateresult;

public enum MatchStatus {

    // Вместо этого enum можно добавить в MatchStateDTO поле String winnerName
        // и по нему определять, что матч завершён (если оно будет не null)

    ONGOING,
    FINISHED
}
