package tennis.score.board.model.match;

import lombok.Getter;

import java.util.Optional;

@Getter
public class Score {

    private final static int SETS_TO_WIN = 2;

    private final SideScore points;
    private final SideScore games;
    private final SideScore sets;

    boolean tieBreak;

    public Score() {
        this.points = new SideScore();
        this.games = new SideScore();
        this.sets = new SideScore();
    }

    public void update(WinnerSide winnerSide) {
        updatePoints(winnerSide);
        updateGames();
        updateSets();
    }

    private void updatePoints(WinnerSide winnerSide) {
        switch(winnerSide) {
            case PLAYER_1 -> points.incrementPlayer1();
            case PLAYER_2 -> points.incrementPlayer2();
        }
    }

    private void updateGames() {
        if(tieBreak) {
            updateTieBreak();
            return;
        }

        if(points.getPlayer1() > 2 && points.getPlayer2() > 2) {
            if(points.getPlayer1() - points.getPlayer2() == 2) {
                games.incrementPlayer1();
                points.reset();
            }
            else if(points.getPlayer2() - points.getPlayer1() == 2) {
                games.incrementPlayer2();
                points.reset();
            }
        }
        else if(points.getPlayer1() == 4 && points.getPlayer1() - points.getPlayer2() >= 2) {
            games.incrementPlayer1();
            points.reset();
        }
        else if(points.getPlayer2() == 4 && points.getPlayer2() - points.getPlayer1() >= 2) {
            games.incrementPlayer2();
            points.reset();
        }
    }

    private void updateSets() {
        if(games.getPlayer1() > 4 && games.getPlayer2() > 4) {
            if(games.getPlayer1() == 7 && games.getPlayer2() == 5) {
                sets.incrementPlayer1();
                games.reset();
            }
            else if(games.getPlayer1() == 5 && games.getPlayer2() == 7) {
                sets.incrementPlayer2();
                games.reset();
            }
            else if(shouldStartTieBreak()) {
                tieBreak = true;
            }
        }
        else if(games.getPlayer1() == 6 && games.getPlayer1() - games.getPlayer2() >= 2) {
            sets.incrementPlayer1();
            games.reset();
        }
        else if(games.getPlayer2() == 6 && games.getPlayer2() - games.getPlayer1() >= 2) {
            sets.incrementPlayer2();
            games.reset();
        }
    }

    private void updateTieBreak() {
        if (points.getPlayer1() > 6 || points.getPlayer2() > 6) {
            if (points.getPlayer1() - points.getPlayer2() == 2) {
                sets.incrementPlayer1();
                points.reset();
                games.reset();
                tieBreak = false;
            } else if (points.getPlayer2() - points.getPlayer1() == 2) {
                sets.incrementPlayer2();
                points.reset();
                games.reset();
                tieBreak = false;
            }
        }
    }

    public boolean isOver() {
        return sets.getPlayer1() == SETS_TO_WIN || sets.getPlayer2() == SETS_TO_WIN;
    }

    public Optional<WinnerSide> getWinnerSide() {
        if(!isOver()) {
            return Optional.empty();
        }

        return Optional.of((sets.getPlayer1() == SETS_TO_WIN)
                ? WinnerSide.PLAYER_1
                : WinnerSide.PLAYER_2);
    }

    private boolean shouldStartTieBreak() {
        return games.getPlayer1() == 6 && games.getPlayer2() == 6;
    }

}
