package tennis.score.board.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PlayerNotInMatchException extends RuntimeException {
    public PlayerNotInMatchException(Long id) {

        // Сообщения в исключениях принято писать на английском языке.
        super("Игрок с id " + id.toString() + " не учавствует в матче"); // Опечатка учавствует —> участвует
    }
}
