package tennis.score.board.model.matchstate.set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tennis.score.board.model.matchstate.WinnerSide;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static tennis.score.board.model.matchstate.WinnerSide.PLAYER_1;
import static tennis.score.board.model.matchstate.WinnerSide.PLAYER_2;

class SetScoreTest {

    // Логику набора очков можно вынести во вспомогательный метод, чтобы не дублировать её в тестах

    private SetScore setScore;

    @BeforeEach
    void setUp() {
        setScore = new SetScore();
    }

    @Test
    void shouldWinSetForPlayer1At6_0() {
        Optional<WinnerSide> winner = Optional.empty();

        for (int i = 0; i < 6; i++) {
            winner = winSingleGame(PLAYER_1);
        }

        assertTrue(winner.isPresent());
        assertEquals(PLAYER_1, winner.get());

        SetScoreSnapshot snapshot = setScore.snapshot();
        assertEquals(6, snapshot.player1Games());
        assertEquals(0, snapshot.player2Games());
        assertFalse(snapshot.tieBreak());
        assertEquals("0", snapshot.player1DisplayPoints());
        assertEquals("0", snapshot.player2DisplayPoints());
    }

    @Test
    void shouldWinSetForPlayer2At0_6() {
        Optional<WinnerSide> winner = Optional.empty();

        for (int i = 0; i < 6; i++) {
            winner = winSingleGame(PLAYER_2);
        }

        assertTrue(winner.isPresent());
        assertEquals(PLAYER_2, winner.get());

        SetScoreSnapshot snapshot = setScore.snapshot();
        assertEquals(0, snapshot.player1Games());
        assertEquals(6, snapshot.player2Games());
        assertFalse(snapshot.tieBreak());
        assertEquals("0", snapshot.player1DisplayPoints());
        assertEquals("0", snapshot.player2DisplayPoints());
    }

    @Test
    void shouldWinSetForPlayer1At6_4() {
        Optional<WinnerSide> winner = Optional.empty();

        for (int i = 0; i < 4; i++) {
            winner = winSingleGame(PLAYER_2);
        }
        for (int i = 0; i < 6; i++) {
            winner = winSingleGame(PLAYER_1);
        }

        assertTrue(winner.isPresent());
        assertEquals(PLAYER_1, winner.get());

        SetScoreSnapshot snapshot = setScore.snapshot();
        assertEquals(6, snapshot.player1Games());
        assertEquals(4, snapshot.player2Games());
        assertFalse(snapshot.tieBreak());
    }

    @Test
    void shouldWinSetForPlayer2At4_6() {
        Optional<WinnerSide> winner = Optional.empty();

        for (int i = 0; i < 4; i++) {
            winner = winSingleGame(PLAYER_1);
        }
        for (int i = 0; i < 6; i++) {
            winner = winSingleGame(PLAYER_2);
        }

        assertTrue(winner.isPresent());
        assertEquals(PLAYER_2, winner.get());

        SetScoreSnapshot snapshot = setScore.snapshot();
        assertEquals(4, snapshot.player1Games());
        assertEquals(6, snapshot.player2Games());
        assertFalse(snapshot.tieBreak());
    }

    @Test
    void shouldNotWinSetAt6_5() {
        Optional<WinnerSide> winner = Optional.empty();

        for (int i = 0; i < 5; i++) {
            winner = winSingleGame(PLAYER_2);
        }
        for (int i = 0; i < 6; i++) {
            winner = winSingleGame(PLAYER_1);
        }
        assertTrue(winner.isEmpty());

        SetScoreSnapshot snapshot = setScore.snapshot();
        assertEquals(6, snapshot.player1Games());
        assertEquals(5, snapshot.player2Games());
        assertFalse(snapshot.tieBreak());
    }

    @Test
    void shouldNotWinSetAt5_6() {
        Optional<WinnerSide> winner = Optional.empty();

        for (int i = 0; i < 5; i++) {
            winner = winSingleGame(PLAYER_1);
        }
        for (int i = 0; i < 6; i++) {
            winner = winSingleGame(PLAYER_2);
        }
        assertTrue(winner.isEmpty());

        SetScoreSnapshot snapshot = setScore.snapshot();
        assertEquals(5, snapshot.player1Games());
        assertEquals(6, snapshot.player2Games());
        assertFalse(snapshot.tieBreak());
    }

    @Test
    void shouldStartTieBreakAt6_6() {
        Optional<WinnerSide> winner;

        for (int i = 0; i < 5; i++) {
            winSingleGame(PLAYER_1);
            winSingleGame(PLAYER_2);
        }
        winSingleGame(PLAYER_1);
        winner = winSingleGame(PLAYER_2);

        assertTrue(winner.isEmpty());

        SetScoreSnapshot snapshot = setScore.snapshot();
        assertEquals(6, snapshot.player1Games());
        assertEquals(6, snapshot.player2Games());
        assertTrue(snapshot.tieBreak());
        assertEquals("0", snapshot.player1DisplayPoints());
        assertEquals("0", snapshot.player2DisplayPoints());
    }

    @Test
    void shouldNotFinishTieBreakAt7_6() {
        reachSixAll();

        Optional<WinnerSide> winner;

        for (int i = 0; i < 6; i++) {
            setScore.pointWonBy(PLAYER_1);
        }
        for (int i = 0; i < 6; i++) {
            setScore.pointWonBy(PLAYER_2);
        }
        winner = setScore.pointWonBy(PLAYER_1);

        assertTrue(winner.isEmpty());

        SetScoreSnapshot snapshot = setScore.snapshot();
        assertTrue(snapshot.tieBreak());
        assertEquals("7", snapshot.player1DisplayPoints());
        assertEquals("6", snapshot.player2DisplayPoints());
    }

    @Test
    void shouldWinTieBreakSetForPlayer1At8_6() {
        reachSixAll();

        Optional<WinnerSide> winner;

        for (int i = 0; i < 6; i++) {
            setScore.pointWonBy(PLAYER_1);
        }
        for (int i = 0; i < 6; i++) {
            setScore.pointWonBy(PLAYER_2);
        }
        setScore.pointWonBy(PLAYER_1);
        winner = setScore.pointWonBy(PLAYER_1);

        assertTrue(winner.isPresent());
        assertEquals(PLAYER_1, winner.get());

        SetScoreSnapshot snapshot = setScore.snapshot();
        assertFalse(snapshot.tieBreak());
        assertEquals(7, snapshot.player1Games());
        assertEquals(6, snapshot.player2Games());
        assertEquals("0", snapshot.player1DisplayPoints());
        assertEquals("0", snapshot.player2DisplayPoints());
    }

    @Test
    void shouldWinTieBreakSetForPlayer2At6_8() {
        reachSixAll();

        Optional<WinnerSide> winner;

        for (int i = 0; i < 6; i++) {
            setScore.pointWonBy(PLAYER_1);
        }
        for (int i = 0; i < 6; i++) {
            setScore.pointWonBy(PLAYER_2);
        }
        setScore.pointWonBy(PLAYER_2);
        winner = setScore.pointWonBy(PLAYER_2);

        assertTrue(winner.isPresent());
        assertEquals(PLAYER_2, winner.get());

        SetScoreSnapshot snapshot = setScore.snapshot();
        assertFalse(snapshot.tieBreak());
        assertEquals(6, snapshot.player1Games());
        assertEquals(7, snapshot.player2Games());
        assertEquals("0", snapshot.player1DisplayPoints());
        assertEquals("0", snapshot.player2DisplayPoints());
    }

    private Optional<WinnerSide> winSingleGame(WinnerSide winnerSide) {
        Optional<WinnerSide> winner = Optional.empty();

        for (int i = 0; i < 4; i++) {
            winner = setScore.pointWonBy(winnerSide);
        }
        return winner;
    }

    private void reachSixAll() {
        for (int i = 0; i < 5; i++) {
            winSingleGame(PLAYER_1);
            winSingleGame(PLAYER_2);
        }
        winSingleGame(PLAYER_1);
        winSingleGame(PLAYER_2);
    }
}