package tennis.score.board.model.matchstate.game.deuce;

public enum DeucePoints {
    DEUCE("40", "40"),
    ADVANTAGE_P1("AD", "40"),
    ADVANTAGE_P2("40", "AD");

    private final String displayValuePlayer1;
    private final String displayValuePlayer2;


    DeucePoints(String displayValuePlayer1, String displayValuePlayer2) {
        this.displayValuePlayer1 = displayValuePlayer1;
        this.displayValuePlayer2 = displayValuePlayer2;
    }

    String getDisplayValuePlayer1() {
        return this.displayValuePlayer1;
    }

    String getDisplayValuePlayer2() {
        return this.displayValuePlayer2;
    }

}
