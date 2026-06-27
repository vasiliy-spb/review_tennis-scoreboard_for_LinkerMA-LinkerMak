package tennis.score.board.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(
        name = "players",
        indexes = @Index(name = "idx_players_name", columnList = "name"),
        uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Player {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, length = 100)
    private String name;

    public Player(String name) {
        this.name = name;
    }
}
