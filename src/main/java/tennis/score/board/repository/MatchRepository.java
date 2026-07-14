package tennis.score.board.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import tennis.score.board.model.entity.Match;

import java.util.List;

@Repository
public class MatchRepository {

    // Можно использовать Spring Data JPA (spring-data-jpa).
        // Это позволит значительно сократить код и использовать удобные интерфейсы Page и Pageable.

    // TODO: Нет интерфейса для этого класса.
        // Это нарушение Принципа инверсии зависимостей (Dependency Inversion Principle):
        // Принцип гласит, что модули верхних уровней не должны зависеть от модулей нижних уровней,
        // а также они должны зависеть от абстракций. В данном случае вышестоящие модули (сервисы)
        // напрямую зависят от конкретных реализаций репозиториев, что делает систему жёстко связанной и хрупкой.

    // Константы объявляются первыми (пишутся в самом верху) в классе.

    // Ключевые слова в тексте JPQL-запроса (`from`, `where` и др.) написаны в нижнем регистре.
        // Хотя это и не влияет на работоспособность, написание ключевых слов SQL/HQL в верхнем регистре (`UPPERCASE`) является общепринятым стандартом.
        // Это значительно улучшает читаемость запросов, так как визуально отделяет синтаксические конструкции языка от имён сущностей и полей.

    // TODO: Тело каждого метода стоит обернуть в try-catch и отлавливать исключения при работе с БД.
        // Слой репозиториев должен перехватывать специфичные для технологии исключения
        // и оборачивать их в свои исключения слоя доступа к данным.
        // Это скрывает детали реализации от верхних слоёв и делает их независимыми от деталей реализации репозиториев.

    // TODO: Проблема N+1 запросов в методе выборки матчей.
        // Методы выборки списка матчей выполняют JPQL-запросы вида `"FROM Match m ..."`.
        // Сущность `Match` имеет связи `@ManyToOne` с `Player`, поэтому при выполнении такого запроса
        // Hibernate сначала получит список матчей (1 запрос), а затем он будет выполнять по 2 дополнительных `SELECT` запроса
        // для каждого матча, чтобы получить связанных с ним игроков. Если на странице 10 матчей,
        // это приведёт к 21 запросу (если все игроки будут разные) вместо одного.

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

    // Можно просто countAll
    public long countFinishedMatches(String playerName) {
        return entityManager.createQuery(COUNT_FINISHED_MATCHES_JPQL_WITH_NAME_FILTER, Long.class)
                .setParameter("playerName", normalizedNameFilter(playerName))
                .getSingleResult();
    }

    // Можно просто countAll
    public long countFinishedMatches() {
        return entityManager.createQuery(COUNT_FINISHED_MATCHES_JPQL, Long.class)
                .getSingleResult();
    }

    // Можно просто findAll
    public List<Match> findAllMatches(int offset, int pageSize) {
        return entityManager.createQuery(FIND_ALL_MATCHES, Match.class)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
    }

    // Можно просто findAll
    public List<Match> findAllMatches(String playerName, int offset, int pageSize) {
        return entityManager.createQuery(FIND_ALL_MATCHES_WITH_NAME_FILTER, Match.class)
                .setParameter("playerName", normalizedNameFilter(playerName))
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
    }

    // Этот метод может быть не статическим
    private static String normalizedNameFilter(String nameFilter) {
        return "%" + nameFilter.trim().toLowerCase() + "%";
    }
}
