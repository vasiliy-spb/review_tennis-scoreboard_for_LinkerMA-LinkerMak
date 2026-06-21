package tennis.score.board.web.dto;


public record MatchDTO (
        String player1Name,
        String player2Name,
        String winnerName
) { }
