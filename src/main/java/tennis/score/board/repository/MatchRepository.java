package tennis.score.board.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import tennis.score.board.model.entity.Match;

import java.util.List;

@Repository
public class MatchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Match match) {
        entityManager.persist(match);
    }

    public long countFinishedMatches(String playerName) {
        String jpql = """
                select count(m) 
                from Match m
                where lower(m.player1.name) like :playerName
                or lower(m.player2.name) like :playerName
                """;
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("playerName", normalizedNameFilter(playerName))
                .getSingleResult();
    }

    public long countFinishedMatches() {
        String jpql = """
                select count(m) 
                from Match m
                """;
        return entityManager.createQuery(jpql, Long.class)
                .getSingleResult();
    }

    public List<Match> findAllMatches(int offset, int pageSize) {
        String jpql = """
                select m
                from Match m
                order by m.id desc
                """;
        return entityManager.createQuery(jpql, Match.class)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public List<Match> findAllMatches(String playerName, int offset, int pageSize) {
        String jpql = """
                select m
                from Match m
                where lower(m.player1.name) like :playerName
                or lower(m.player2.name) like :playerName
                order by m.id desc
                """;
        return entityManager.createQuery(jpql, Match.class)
                .setParameter("playerName", normalizedNameFilter(playerName))
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
    }

    private static String normalizedNameFilter(String nameFilter) {
        return "%" + nameFilter.trim().toLowerCase() + "%";
    }
}
