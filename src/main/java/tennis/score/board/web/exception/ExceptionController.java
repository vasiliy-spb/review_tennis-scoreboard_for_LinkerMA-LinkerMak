package tennis.score.board.web.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tennis.score.board.exception.BadRequestException;
import tennis.score.board.exception.EntityNotFoundException;
import tennis.score.board.exception.PlayerNotInMatchException;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    // Можно назвать ExceptionHandler

    // Все повторяющиеся или важные строковые литералы лучше выносить в `private static final` константы с понятными именами.
        // Именованная константа делает код более семантически понятным.

    // Отсутствует явное указание модификатора доступа на методах

    // TODO: Класс отправляет сообщение из исключения (`e.getMessage()`) напрямую пользователю.
        // Сообщения об ошибках из исключений могут содержать технические детали, которые не предназначены
        // для конечного пользователя и могут представлять угрозу безопасности. Например, сообщение может быть
        // `"No entity found for query 'SELECT ...'"` или `"Validation failed for field 'internalFieldName'"`,
        // что раскрывает структуру БД или внутренние имена полей.
        //
        // Лучше никогда не отправлять необработанное сообщение из исключения на клиент.
        // Вместо этого можно использовать заранее определённые, безопасные сообщения или коды ошибок.
        // Само исключение при этом нужно логировать для разработчиков.
        //
        // Это повысит безопасность приложения и улучшит пользовательский опыт при возникновении ошибок.
        //
        // Допустимо оставить e.getMessage() для ошибок валидации.

    @ExceptionHandler(Exception.class)
    String handleException(Exception e, HttpServletRequest request, HttpServletResponse response, Model model) {
        log.error("Unhandled exception. path={}, message={}", request.getRequestURI(), e.getMessage(), e);

        addAttributesAndSetStatus("Внутренняя ошибка сервера: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request,
                response,
                model);
        return "error-page";
    }

    @ExceptionHandler(PlayerNotInMatchException.class)
    String playerNotInMatch(PlayerNotInMatchException e, HttpServletRequest request, HttpServletResponse response, Model model) {
        log.warn("Player not in match. path={}, message={}", request.getRequestURI(), e.getMessage());

        addAttributesAndSetStatus(e.getMessage(),
                HttpStatus.BAD_REQUEST,
                request,
                response,
                model);
        return "error-page";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    String entityNotFound(EntityNotFoundException e, HttpServletRequest request, HttpServletResponse response, Model model) {
        log.warn("Entity not found. path={}, message={}", request.getRequestURI(), e.getMessage());

        addAttributesAndSetStatus(e.getMessage(),
                HttpStatus.NOT_FOUND,
                request,
                response,
                model);
        return "error-page";
    }

    @ExceptionHandler(BadRequestException.class)
    String badRequestException(BadRequestException e, HttpServletRequest request, HttpServletResponse response, Model model){
        log.warn("Bad request. path={}, message={}", request.getRequestURI(), e.getMessage());

        addAttributesAndSetStatus(e.getMessage(),
                HttpStatus.BAD_REQUEST,
                request,
                response,
                model);
        return "error-page";
    }

    private void addAttributesAndSetStatus(String message, HttpStatus status, HttpServletRequest request, HttpServletResponse response, Model model) {
        response.setStatus(status.value());

        model.addAttribute("message", message);
        model.addAttribute("status", status.value());
        model.addAttribute("error", status.getReasonPhrase());
        model.addAttribute("path", request.getRequestURI());
    }
}
