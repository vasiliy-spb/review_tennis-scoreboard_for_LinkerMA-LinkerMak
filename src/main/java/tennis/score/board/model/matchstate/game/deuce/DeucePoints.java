package tennis.score.board.model.matchstate.game.deuce;

public enum DeucePoints {

    // TODO: Выделять в отдельную абстракцию счёт для режима больше-меньше — избыточно.
        // Значение 40 уже есть в Points и там же уместно разместить значение для "AD".

    DEUCE("40", "40"),
    ADVANTAGE_P1("AD", "40"),
    ADVANTAGE_P2("40", "AD");

    private final String displayValuePlayer1;
    private final String displayValuePlayer2;


    DeucePoints(String displayValuePlayer1, String displayValuePlayer2) {
        this.displayValuePlayer1 = displayValuePlayer1;
        this.displayValuePlayer2 = displayValuePlayer2;
    }

    // Отсутствует явное указание модификатора доступа
    String getDisplayValuePlayer1() {
        return this.displayValuePlayer1;
    }

    // Отсутствует явное указание модификатора доступа
    String getDisplayValuePlayer2() {
        return this.displayValuePlayer2;
    }

}
