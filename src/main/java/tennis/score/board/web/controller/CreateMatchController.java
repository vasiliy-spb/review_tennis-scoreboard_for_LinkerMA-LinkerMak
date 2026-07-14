package tennis.score.board.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tennis.score.board.model.entity.Player;
import tennis.score.board.service.OngoingMatchService;
import tennis.score.board.service.PlayerService;
import tennis.score.board.web.validator.PlayerNameValidator;


import java.util.UUID;


@Controller
public class CreateMatchController {

    // Все повторяющиеся или важные строковые литералы лучше выносить в `private static final` константы с понятными именами.
        // Именованная константа делает код более семантически понятным.

    // TODO: После валидации имён игроков, контроллер получает JPA Entity игроков (`Player`) из `PlayerService` только для того, чтобы передать их в `OngoingMatchService.createMatch(player1, player2)`.
        // Это нарушает границы между слоями приложения и Принцип разделения ответственности
        // (см. файл "separation-of-concerns-principle.md" в этом же пакете).
        // Контроллер не должен работать с JPA сущностями и знать о существовании класса `Player` — ему это не нужно для выполнения его задачи.
        // Он должен общаться с сервисным слоем исключительно через объекты передачи данных (DTO).
        //
        // Сервисный слой должен возвращать только те данные, которые необходимы контроллеру.
        // В данном случае, сервлету нужен только ID созданного матча для редиректа.
        // Идеальная картина для него — использовать только один сервис (например, `OngoingMatchesService`) —
        // отправлять ему входящие данные и получать ответ, который нужно отдать в представление.
        // А логикой создания матча пусть управляет сервисный слой. Такой рефакторинг сделает контроллер "тонким"
        // (см. файл "fat-controller.md" в этом же пакете)
        // и его единственной задачей останется обработка HTTP и делегирование бизнес-запроса сервисному слою.

    private final PlayerService playerService;
    private final OngoingMatchService ongoingMatchService;

    // Если у класса есть ровно один конструктор, Spring автоматически использует его для внедрения зависимостей —
        // даже без @Autowired. Можно удалить конструктор и поставить над классом @RequiredArgsConstructor
    @Autowired
    public CreateMatchController(PlayerService playerService, OngoingMatchService ongoingMatchService) {
        this.playerService = playerService;
        this.ongoingMatchService = ongoingMatchService;
    }

    @GetMapping("/new")
    public String newMatch() {
        return "new-match";
    }

    @PostMapping("/new-match")
    public String createMatch(@RequestParam("playerOne") String namePlayer1,
                              @RequestParam("playerTwo") String namePlayer2) {

        String normalizeName1 = PlayerNameValidator.normalizeAndValidate(namePlayer1);
        String normalizeName2 = PlayerNameValidator.normalizeAndValidate(namePlayer2);
        PlayerNameValidator.validateDifferentPlayers(normalizeName1, normalizeName2);

        // TODO: Контроллер не должен работать с Entity — эта логика должна быть в сервисе
        Player player1 = playerService.findOrCreate(normalizeName1);
        Player player2 = playerService.findOrCreate(normalizeName2);

        UUID uuid = ongoingMatchService.createMatch(player1, player2);

        return "redirect:/match-score?uuid=" + uuid.toString();
    }

}
