package tennis.score.board.model.matchstate.game.deuce;

import tennis.score.board.model.matchstate.WinnerSide;
import tennis.score.board.model.matchstate.game.GameResult;
import tennis.score.board.model.matchstate.game.GameScore;
import tennis.score.board.model.matchstate.game.GameScoreSnapshot;

import java.util.Optional;

public class DeuceGameScore implements GameScore {

    DeucePoints pointsState = DeucePoints.DEUCE;

    @Override
    public GameResult pointWonBy(WinnerSide winnerSide) {
        return switch(pointsState) {
            case DeucePoints.DEUCE -> handleDueceState(winnerSide);
            case DeucePoints.ADVANTAGE_P1 -> handleAdvantage_P1(winnerSide);
            case DeucePoints.ADVANTAGE_P2 -> handleAdvantage_P2(winnerSide);
        };
    }

    private GameResult handleDueceState(WinnerSide winnerSide) {
        if(winnerSide == WinnerSide.PLAYER_1) {
            pointsState = DeucePoints.ADVANTAGE_P1;
        }

        if(winnerSide == WinnerSide.PLAYER_2) {
            pointsState = DeucePoints.ADVANTAGE_P2;
        }

        return GameResult.CONTINUES;
    }

    private GameResult handleAdvantage_P1(WinnerSide winnerSide) {
        if(winnerSide == WinnerSide.PLAYER_1) {
            return GameResult.FINISHED;
        }
        else {
            pointsState = DeucePoints.DEUCE;
            return GameResult.CONTINUES;
        }
    }

    private GameResult handleAdvantage_P2(WinnerSide winnerSide) {
        if(winnerSide == WinnerSide.PLAYER_2) {
            return GameResult.FINISHED;
        }
        else {
            pointsState = DeucePoints.DEUCE;
            return GameResult.CONTINUES;
        }
    }

    @Override
    public Optional<WinnerSide> getWinner() {
        return Optional.of(switch(pointsState) {
            case ADVANTAGE_P1 -> WinnerSide.PLAYER_1;
            case ADVANTAGE_P2 -> WinnerSide.PLAYER_2;
            case DEUCE -> throw new IllegalStateException("Не удалось определить победителя деюса");
        });
    }

    @Override
    public GameScoreSnapshot snapshot() {
        return new GameScoreSnapshot(
                pointsState.getDisplayValuePlayer1(),
                pointsState.getDisplayValuePlayer2()
        );
    }
}
