package tennis.score.board.model.matchstate.game.regulargame;

public enum Points {

    // В реальном теннисном гейме также есть значение ADVANTAGE, обозначающее преимущество

    LOVE(0, "0"),
    FIFTEEN(1, "15"),
    THIRTY(2, "30"),
    FORTY(3, "40"),
    WIN_POINT(4, ""); // В реальном теннисном гейме нет счёта с таким значением

    // Поле int rank кодирует значения enum (и предметной области), что лишает смысла от использования перечисления.
    private final int rank;
    private final String displayValue;

    Points(int rank, String displayValue) {
        this.rank = rank;
        this.displayValue = displayValue;
    }

    public Points next() {
        return switch (this) {
            case LOVE -> FIFTEEN;
            case FIFTEEN -> THIRTY;
            case THIRTY -> FORTY;
            case FORTY -> WIN_POINT;

            // Сообщения в исключениях принято писать на английском языке.
            case WIN_POINT -> throw new IllegalStateException("Нельзя вызывать next() для WIN_POINT");
        };
    }

    public int rank() {
        return this.rank;
    }

    // Доменная модель не должна знать то, как она отображается во View — это нарушает Принцип единой ответственности (SRP).
        // В идеале эта логика должна быть в маппере.
    public String displayValue() {
        if (this == Points.WIN_POINT) {

            // Сообщения в исключениях принято писать на английском языке.
            throw new IllegalStateException("Нельзя вызывать displayValue() для WIN_POINT");
        }
        return this.displayValue;
    }

}
