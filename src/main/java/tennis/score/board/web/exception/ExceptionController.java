package tennis.score.board.web.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tennis.score.board.exception.BadRequestException;
import tennis.score.board.exception.EntityNotFoundException;
import tennis.score.board.exception.PlayerNotInMatchException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    String handleException(Exception e, HttpServletRequest request, HttpServletResponse response, Model model) {
        addAttributesAndSetStatus("Внутренняя ошибка сервера: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request,
                response,
                model);
        return "error-page";
    }

    @ExceptionHandler(PlayerNotInMatchException.class)
    String playerNotInMatch(PlayerNotInMatchException e, HttpServletRequest request, HttpServletResponse response, Model model) {
        addAttributesAndSetStatus(e.getMessage(),
                HttpStatus.BAD_REQUEST,
                request,
                response,
                model);
        return "error-page";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    String entityNotFound(EntityNotFoundException e, HttpServletRequest request, HttpServletResponse response, Model model) {
        addAttributesAndSetStatus(e.getMessage(),
                HttpStatus.NOT_FOUND,
                request,
                response,
                model);
        return "error-page";
    }

    @ExceptionHandler(BadRequestException.class)
    String badRequestException(BadRequestException e, HttpServletRequest request, HttpServletResponse response, Model model){
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
