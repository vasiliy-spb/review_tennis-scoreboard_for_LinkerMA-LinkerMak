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

    // TODO: Нет интерфейса для этого класса. (см. файл "service.md" в этом же пакете)

    private final MatchMapper matchMapper;
    private final MatchRepository matchRepository;

    // Константы объявляются первыми (пишутся в самом верху) в классе.
    // Размер страницы и номер по умолчанию более уместно хранить в контроллере, так как в идеале он должен приходить с фронтенда.
        // А сервис должен принимать это значение в качестве аргумента в методы.
    private static final int PAGE_SIZE = 2;

    // Если у класса есть ровно один конструктор, Spring автоматически использует его для внедрения зависимостей —
        // даже без @Autowired. Можно удалить конструктор и поставить над классом @RequiredArgsConstructor
    @Autowired
    public MatchService(MatchMapper matchMapper, MatchRepository matchRepository) {
        this.matchMapper = matchMapper;
        this.matchRepository = matchRepository;
    }

    @Transactional
    public void saveMatch(Match match) {
        matchRepository.save(match);
    }

    // Лучше использовать @Transactional(readOnly = true) – это улучшит производительность и явно выражает намерение
    @Transactional
    public MatchesPage getFinishedMatches(Integer pageNumber, String name) {

        // Тело блока if всегда нужно оборачивать в {}
        if(pageNumber == null || pageNumber < 1) pageNumber = 1;
        String normalizedName = normalizedName(name);

        long totalMatches = (normalizedName == null)
                ? matchRepository.countFinishedMatches()
                : matchRepository.countFinishedMatches(normalizedName);

        // TODO: offset вычисляется до нормализации pageNumber.
            // Если пользователь запросит страницу, превышающую общее количество страниц,
            // offset будет рассчитан на основе некорректного номера страницы,
            // а затем pageNumber будет исправлен в меньшую сторону, но offset уже не пересчитывается.
            // Пример:
            // PAGE_SIZE = 2
            // Всего матчей: 5 —> totalPages = 3
            // Пользователь запрашивает pageNumber = 5
            // offset = (5 - 1) * 2 = 8 — некорректно (допустимые страницы 1-3)
            // Затем pageNumber становится min(5, 3) = 3
            // Запрос к БД: findAllMatches(offset = 8, limit = 2) вернёт пустой список,
                // хотя на странице 3 должны быть записи с индексами 4 и 5 (правильный offset = 4).
        int offset = calculateOffset(pageNumber);
        int totalPages = calculateTotalPages(totalMatches);
        pageNumber = normalizedPageNumber(pageNumber, totalPages);

        List<MatchDTO> matches = ((normalizedName == null)
                ? matchRepository.findAllMatches(offset, PAGE_SIZE)
                : matchRepository.findAllMatches(normalizedName, offset, PAGE_SIZE))
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
                normalizedName
        );
    }

    // Этот метод может быть не статическим
    private static int calculateOffset(int pageNumber) {
        return (pageNumber - 1) * PAGE_SIZE;
    }

    private int calculateTotalPages(long countMatches) {
        return Math.max((int) ceil((double) countMatches / PAGE_SIZE), 1);
    }

    // Этот метод может быть не статическим
    private static String normalizedName(String name) {

        // Тело блока if всегда нужно оборачивать в {}
        if(name == null) return null;

        String trimmed = name.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    // Этот метод может быть не статическим
    private static Integer normalizedPageNumber(Integer pageNumber, Integer totalPages) {
        return Math.min((pageNumber == null || pageNumber < 1) ? 1 : pageNumber, totalPages);
    }

}
