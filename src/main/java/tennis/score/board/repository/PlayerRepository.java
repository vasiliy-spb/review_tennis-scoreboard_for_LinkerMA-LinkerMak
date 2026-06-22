package tennis.score.board.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import tennis.score.board.model.entity.Player;

import java.util.Optional;

@Repository
public class PlayerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Player> findByName(String name) {
        return entityManager.createQuery("select p from Player p where p.name = :name", Player.class)
                .setParameter("name", name)
                .getResultList()
                .stream()
                .findFirst();
    }

    public Player save(Player player) {
        entityManager.persist(player);
        return player;
    }

    public Player saveAndFlush(Player player) {
        save(player);
        entityManager.flush();
        return player;
    }
}
