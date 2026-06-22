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
        return playerRepository.findByName(trimmedName)
                .orElseGet(() -> createOrGetExisting(trimmedName));
    }

    private Player createOrGetExisting(String name) {
        try{
            Player newPlayer = new Player(name);
            return playerRepository.saveAndFlush(newPlayer);
        }catch(DataIntegrityViolationException e) {
            return playerRepository.findByName(name)
                    .orElseThrow(() -> new IllegalStateException("Не удалось получить или создать игрока с именем " + name, e));
        }
    }
 }
