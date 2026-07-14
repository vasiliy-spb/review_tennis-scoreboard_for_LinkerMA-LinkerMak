package tennis.score.board.service.updateresult;

import tennis.score.board.web.dto.MatchStateDTO;

public record UpdateMatchResult(

        // Можно назвать OngoingMatchDTO

        MatchStatus status,
        MatchStateDTO matchState
) {
}
