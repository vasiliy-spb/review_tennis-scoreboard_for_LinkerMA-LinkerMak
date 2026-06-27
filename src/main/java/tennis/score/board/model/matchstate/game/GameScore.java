package tennis.score.board.model.matchstate.game;

import tennis.score.board.model.matchstate.WinnerSide;

import java.util.Optional;

public interface GameScore {

    GameResult pointWonBy(WinnerSide winnerSide);

    Optional<WinnerSide> getWinner();

    GameScoreSnapshot snapshot();
}
