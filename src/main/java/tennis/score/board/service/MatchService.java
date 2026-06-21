package tennis.score.board.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tennis.score.board.model.entity.Match;
import tennis.score.board.repository.MatchRepository;
import tennis.score.board.web.dto.MatchDTO;
import tennis.score.board.web.dto.MatchesPage;
import tennis.score.board.web.mapper.MatchMapper;

import java.util.List;

import static java.lang.Math.ceil;

@Service
public class MatchService {

    private final MatchMapper matchMapper;
    private final MatchRepository matchRepository;

    private static final int PAGE_SIZE = 10;

    @Autowired
    public MatchService(MatchMapper matchMapper, MatchRepository matchRepository) {
        this.matchMapper = matchMapper;
        this.matchRepository = matchRepository;
    }

    @Transactional
    public void saveMatch(Match match) {
        matchRepository.save(match);
    }

    @Transactional
    public MatchesPage getFinishedMatches(Integer pageNumber, String name) {
        if(pageNumber == null || pageNumber < 1) pageNumber = 1;
        String normalizedName = normalizedName(name);

        long totalMatches = (normalizedName == null)
                ? matchRepository.countFinishedMatches()
                : matchRepository.countFinishedMatches(name);

        int offset = calculateOffset(pageNumber);
        int totalPages = calculateTotalPages(totalMatches);

        List<MatchDTO> matches = ((normalizedName == null)
                ? matchRepository.findAllMatches(offset, PAGE_SIZE)
                : matchRepository.findAllMatches(name, offset, PAGE_SIZE))
                .stream()
                .map(matchMapper::toMatchDTO)
                .toList();

        return new MatchesPage(
                matches,
                totalMatches,
                pageNumber,
                totalPages,
                PAGE_SIZE,
                pageNumber > 1,
                pageNumber < totalPages,
                name
        );
    }

    private static int calculateOffset(int pageNumber) {
        return (pageNumber - 1) * PAGE_SIZE;
    }

    private int calculateTotalPages(long countMatches) {
        return (int) ceil((double) countMatches / PAGE_SIZE);
    }

    private static String normalizedName(String name) {
        if(name == null) return null;

        String trimmed = name.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

}
