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

    private static final String COUNT_FINISHED_MATCHES_JPQL = """
                select count(m) 
                from Match m
                """;

    private static final String COUNT_FINISHED_MATCHES_JPQL_WITH_NAME_FILTER = """
                select count(m) 
                from Match m
                where lower(m.player1.name) like :playerName
                or lower(m.player2.name) like :playerName
                """;

    private static final String FIND_ALL_MATCHES = """
                select m
                from Match m
                order by m.id desc
                """;

    private static final String FIND_ALL_MATCHES_WITH_NAME_FILTER = """
                select m
                from Match m
                where lower(m.player1.name) like :playerName
                or lower(m.player2.name) like :playerName
                order by m.id desc
                """;

    public void save(Match match) {
        entityManager.persist(match);
    }

    public long countFinishedMatches(String playerName) {
        return entityManager.createQuery(COUNT_FINISHED_MATCHES_JPQL_WITH_NAME_FILTER, Long.class)
                .setParameter("playerName", normalizedNameFilter(playerName))
                .getSingleResult();
    }

    public long countFinishedMatches() {
        return entityManager.createQuery(COUNT_FINISHED_MATCHES_JPQL, Long.class)
                .getSingleResult();
    }

    public List<Match> findAllMatches(int offset, int pageSize) {
        return entityManager.createQuery(FIND_ALL_MATCHES, Match.class)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public List<Match> findAllMatches(String playerName, int offset, int pageSize) {
        return entityManager.createQuery(FIND_ALL_MATCHES_WITH_NAME_FILTER, Match.class)
                .setParameter("playerName", normalizedNameFilter(playerName))
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
    }

    private static String normalizedNameFilter(String nameFilter) {
        return "%" + nameFilter.trim().toLowerCase() + "%";
    }
}
