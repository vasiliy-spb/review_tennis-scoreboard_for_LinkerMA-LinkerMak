package tennis.score.board.model.match;

import lombok.Getter;
import tennis.score.board.exception.PlayerNotInMatchException;
import tennis.score.board.model.entity.Player;

import java.util.Objects;
import java.util.Optional;

import static tennis.score.board.model.match.WinnerSide.PLAYER_1;
import static tennis.score.board.model.match.WinnerSide.PLAYER_2;

@Getter
public class MatchState {

    private final Player player1;
    private final Player player2;
    private final Score score;

    public MatchState(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.score = new Score();
    }

    public void updateScore(Long winnerId) {
        WinnerSide winnerSide = (Objects.equals(winnerId, player1.getId()))
                ? PLAYER_1
                : PLAYER_2;

        score.update(winnerSide);
    }

    public Optional<Player> getWinner() {
        return score.getWinnerSide().map(side -> switch (side) {
            case PLAYER_1 -> player1;
            case PLAYER_2 -> player2;
        });
    }

    public boolean isOver() {
        return score.isOver();
    }

    public void validatePlayerBelongsToMatch(Long id) {
        if(!Objects.equals(id, player1.getId()) && !Objects.equals(id, player2.getId())) {
            throw new PlayerNotInMatchException(id);
        }
    }

}
