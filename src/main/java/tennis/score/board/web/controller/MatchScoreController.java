package tennis.score.board.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tennis.score.board.service.OngoingMatchService;
import tennis.score.board.service.updateresult.MatchStatus;
import tennis.score.board.service.updateresult.UpdateMatchResult;
import tennis.score.board.web.dto.MatchStateDTO;

import java.util.UUID;

@Controller
public class MatchScoreController {

    // Все повторяющиеся или важные строковые литералы лучше выносить в `private static final` константы с понятными именами.
        // Именованная константа делает код более семантически понятным.

    private final OngoingMatchService ongoingMatchService;

    // Если у класса есть ровно один конструктор, Spring автоматически использует его для внедрения зависимостей —
        // даже без @Autowired. Можно удалить конструктор и поставить над классом @RequiredArgsConstructor
    @Autowired
    public MatchScoreController(OngoingMatchService ongoingMatchService) {
        this.ongoingMatchService = ongoingMatchService;
    }

    @GetMapping("/match-score")
    public String getMatchScore(@RequestParam("uuid") UUID uuid,
                                Model model){
        MatchStateDTO matchState = ongoingMatchService.getMatchByUUID(uuid);

        model.addAttribute("matchState", matchState);
        model.addAttribute("uuid", uuid);

        return "match-score";
    }

    @PostMapping("/match-score")
    public String updateMatchScore(@RequestParam("uuid") UUID uuid,
                                   @RequestParam("winnerId") Long winnerId,
                                   RedirectAttributes redirectAttributes) {

        UpdateMatchResult updateMatchResult = ongoingMatchService.updateMatch(uuid, winnerId);

        // Можно после завершения матча показывать финальный счёт на той же странице (match-score)
            // (не выполнять редирект, а просто заблокировать кнопки счёта)
            // и тогда (а также после рефакторинга, предложенного в MatchStatus)
            // необходимость в UpdateMatchResult исчезнет (класс можно будет удалить)
            // и передавать в контроллер только MatchStateDTO, содержащий счёт.
            // Так контроллер станет более тонким. (см. файл "fat-controller.md" в этом же пакете)
        if(updateMatchResult.status() == MatchStatus.FINISHED) {
            redirectAttributes.addFlashAttribute("matchState", updateMatchResult.matchState());
            return "redirect:/finished-match-score";
        }

        return "redirect:/match-score?uuid=" + uuid.toString();
    }

    @GetMapping("/finished-match-score")
    public String getFinishedMatchScore() {
        return "finished-match-score";
    }

}
