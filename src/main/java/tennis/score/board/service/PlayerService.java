package tennis.score.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tennis.score.board.model.entity.Player;
import tennis.score.board.repository.PlayerRepository;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional
    public Player findOrCreate(String name) {
        String trimmedName = name.trim();
        try {
            return playerRepository.findByName(trimmedName)
                    .orElseGet(() -> playerRepository.saveAndFlush(new Player(trimmedName)));
        } catch(DataIntegrityViolationException e) {
            throw new IllegalStateException("Ошибка при создании игрока после поиска " + name, e);        }
    }

 }
