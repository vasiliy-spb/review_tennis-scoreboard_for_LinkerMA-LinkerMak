package tennis.score.board.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Класс называется `HomeController`, а связанная с ним JSP страница `index.jsp`.
        // Можно переименовать класс или HTML страницу, чтобы привести их названия в соответствие.

    // Все повторяющиеся или важные строковые литералы лучше выносить в `private static final` константы с понятными именами.
        // Именованная константа делает код более семантически понятным.

    @GetMapping("/") // Можно зарегистрировать контроллер сразу на несколько подходящих путей: @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

}
