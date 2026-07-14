package tennis.score.board.model.matchstate.game;

import tennis.score.board.model.matchstate.WinnerSide;

import java.util.Optional;

public interface GameScore {

    // TODO: Метод нарушает Принцип разделения команд и запросов (см. файл "cqs-principle.md" в этом же пакете)
    GameResult pointWonBy(WinnerSide winnerSide);

    Optional<WinnerSide> getWinner();

    GameScoreSnapshot snapshot();
}
