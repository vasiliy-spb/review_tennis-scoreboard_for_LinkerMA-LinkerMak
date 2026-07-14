package tennis.score.board.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import tennis.score.board.model.entity.Player;

import java.util.Optional;

@Repository
public class PlayerRepository {

    // Можно использовать Spring Data JPA (spring-data-jpa).
        // Это позволит значительно сократить код и использовать удобные интерфейсы Page и Pageable.

    // TODO: Нет интерфейса для этого класса.
        // Это нарушение Принципа инверсии зависимостей (Dependency Inversion Principle):
        // Принцип гласит, что модули верхних уровней не должны зависеть от модулей нижних уровней,
        // а также они должны зависеть от абстракций. В данном случае вышестоящие модули (сервисы)
        // напрямую зависят от конкретных реализаций репозиториев, что делает систему жёстко связанной и хрупкой.

    // Ключевые слова в тексте JPQL-запроса (`from`, `where`) написаны в нижнем регистре.
        // Хотя это и не влияет на работоспособность, написание ключевых слов SQL/HQL в верхнем регистре (`UPPERCASE`) является общепринятым стандартом.
        // Это значительно улучшает читаемость запросов, так как визуально отделяет синтаксические конструкции языка от имён сущностей и полей.

    // Текст JPQL запроса удобнее читать, когда он логично разбит на строки, даже если он короткий.
        // Для визуального разделения запросов на строки лучше использовать текстовые блоки

    // Лучше вынести текст HQL запроса в `private static final` константу и дать ей понятное имя.

    // Название параметра "name" тоже лучше вынести в именованную константу

    // TODO: Тело каждого метода стоит обернуть в try-catch и отлавливать исключения при работе с БД.
        // Слой репозиториев должен перехватывать специфичные для технологии исключения
        // и оборачивать их в свои исключения слоя доступа к данным.
        // Это скрывает детали реализации от верхних слоёв и делает их независимыми от деталей реализации репозиториев.

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Player> findByName(String name) {
        return entityManager.createQuery("select p from Player p where p.name = :name", Player.class)
                .setParameter("name", name)
                .getResultList() // Лучше использовать специальный метод для получения единственного значения: getSingleResult()
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
