package tennis.score.board.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tennis.score.board.model.entity.Player;
import tennis.score.board.service.MatchService;
import tennis.score.board.service.OngoingMatchService;
import tennis.score.board.service.PlayerService;
import tennis.score.board.service.updateresult.MatchStatus;
import tennis.score.board.service.updateresult.UpdateMatchResult;
import tennis.score.board.web.dto.MatchDTO;
import tennis.score.board.web.dto.MatchStateDTO;
import tennis.score.board.web.dto.MatchesPage;

import java.util.UUID;

@Controller()
@RequestMapping("/matches")
public class MatchController {

    private final PlayerService playerService;
    private final MatchService matchService;
    private final OngoingMatchService ongoingMatchService;

    @Autowired
    public MatchController(PlayerService playerService, MatchService matchService, OngoingMatchService ongoingMatchService) {
        this.playerService = playerService;
        this.matchService = matchService;
        this.ongoingMatchService = ongoingMatchService;
    }

    @GetMapping()
    public String getFinishedMatches(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
                                @RequestParam(value = "filter_by_player_name", required = false) String name,
                                Model model) {

        MatchesPage matchesPage = matchService.getFinishedMatches(pageNumber, name);
        for(MatchDTO match : matchesPage.matches()) {
            System.out.println(match.player1Name() + " ||| " + match.player2Name() + " ||| " + match.winnerName());
            System.out.println("===================");
        }
        model.addAttribute("matchesPage", matchesPage);

        return "matches";
    }

    @GetMapping("/new")
    public String newMatch() {
        return "new-match";
    }

    @PostMapping("/new-match")
    public String createMatch(@RequestParam("playerOne") String namePlayer1,
                              @RequestParam("playerTwo") String namePlayer2) {
        Player player1 = playerService.findOrCreate(namePlayer1);
        Player player2 = playerService.findOrCreate(namePlayer2);

        UUID uuid = ongoingMatchService.createMatch(player1, player2);

        return "redirect:/matches/match-score?uuid=" + uuid.toString();
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

        if(updateMatchResult.status() == MatchStatus.FINISHED) {
            redirectAttributes.addFlashAttribute("matchState", updateMatchResult.matchState());
            return "redirect:/matches/finished-match-score";
        }

        return "redirect:/matches/match-score?uuid=" + uuid.toString();
    }

    @GetMapping("/finished-match-score")
    public String getFinishedMatchScore() {
        return "finished-match-score";
    }
}
