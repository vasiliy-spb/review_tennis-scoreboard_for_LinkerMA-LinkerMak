package tennis.score.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tennis.score.board.model.entity.Player;
import tennis.score.board.repository.PlayerRepository;

@Service
public class PlayerService {

    // TODO: Нет интерфейса для этого класса. (см. файл "service.md" в этом же пакете)

    private final PlayerRepository playerRepository;

    // Если у класса есть ровно один конструктор, Spring автоматически использует его для внедрения зависимостей —
        // даже без @Autowired. Можно удалить конструктор и поставить над классом @RequiredArgsConstructor
    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    // TODO: Создание обоих игроков должно происходить в одной транзакции, которая будет откатываться,
        // если хотя бы один игрок не будет создан. То есть аннотация @Transactional должна быть на методе,
        // который вызывает findOrCreate и этот метод должен быть в сервисе (не в контроллере).
    @Transactional
    public Player findOrCreate(String name) {
        String trimmedName = name.trim();
        try {
            return playerRepository.findByName(trimmedName)
                    .orElseGet(() -> playerRepository.saveAndFlush(new Player(trimmedName)));
        } catch(DataIntegrityViolationException e) {

            // Сообщения в исключениях принято писать на английском языке.
            throw new IllegalStateException("Ошибка при создании игрока после поиска " + name, e);        }
    }

 }
