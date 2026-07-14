package tennis.score.board.model.matchstate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tennis.score.board.model.entity.Player;

import static org.junit.jupiter.api.Assertions.*;
import static tennis.score.board.model.matchstate.WinnerSide.PLAYER_1;
import static tennis.score.board.model.matchstate.WinnerSide.PLAYER_2;

class MatchStateTest {

    // Логику начисления очков должно быть возможным тестировать без участия JPA Entity.
        // Это исправится после рефакторинга классов моделей.

    private Player player1;
    private Player player2;
    private MatchState matchState;

    @BeforeEach
    void setUp() {
        player1 = new Player("Makar");
        player2 = new Player("Makron");
        matchState = new MatchState(player1, player2);
    }

    @Test
    void shouldIncrementPlayer1PointsWhenPointWonByPlayer1() {
        matchState.updateScore(PLAYER_1);

        ScoreboardSnapshot snapshot = matchState.snapshot();

        assertEquals("15", snapshot.player1Points());
        assertEquals("0", snapshot.player2Points());
        assertEquals(0, snapshot.player1Games());
        assertEquals(0, snapshot.player2Games());
        assertEquals(0, snapshot.player1Sets());
        assertEquals(0, snapshot.player2Sets());
        assertFalse(snapshot.tieBreak());
    }

    @Test
    void shouldIncrementPlayer2PointsWhenPointWonByPlayer2() {
        matchState.updateScore(PLAYER_2);

        ScoreboardSnapshot snapshot = matchState.snapshot();

        assertEquals("0", snapshot.player1Points());
        assertEquals("15", snapshot.player2Points());
        assertEquals(0, snapshot.player1Games());
        assertEquals(0, snapshot.player2Games());
        assertEquals(0, snapshot.player1Sets());
        assertEquals(0, snapshot.player2Sets());
        assertFalse(snapshot.tieBreak());
    }

    @Test
    void shouldReturnPlayersNamesInSnapshot() {
        ScoreboardSnapshot snapshot = matchState.snapshot();

        assertEquals("Makar", snapshot.player1Name());
        assertEquals("Makron", snapshot.player2Name());
    }

    @Test
    void shouldNotBeOverAtStart() {
        assertFalse(matchState.isOver());
    }

    @Test
    void shouldThrowWhenGettingWinnerBeforeMatchIsFinished() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> matchState.getMatchWinner()
        );

        assertTrue(exception.getMessage().contains("матч еще не закончен"));
    }

    @Test
    void shouldReturnPlayer1AsWinnerWhenPlayer1WinsMatch() {
        winMatch(PLAYER_1);

        assertTrue(matchState.isOver());
        assertEquals(player1, matchState.getMatchWinner());
    }

    @Test
    void shouldReturnPlayer2AsWinnerWhenPlayer2WinsMatch() {
        winMatch(PLAYER_2);

        assertTrue(matchState.isOver());
        assertEquals(player2, matchState.getMatchWinner());
    }

    @Test
    void shouldKeepMatchNotOverWhenOnlyOneSetIsWon() {
        winSet(PLAYER_1);

        assertFalse(matchState.isOver());

        ScoreboardSnapshot snapshot = matchState.snapshot();
        assertEquals(1, snapshot.player1Sets());
        assertEquals(0, snapshot.player2Sets());
        assertEquals(0, snapshot.player1Games());
        assertEquals(0, snapshot.player2Games());
        assertEquals("0", snapshot.player1Points());
        assertEquals("0", snapshot.player2Points());
    }

    @Test
    void shouldResetGamesAndPointsAfterFinishedSet() {
        winSet(PLAYER_1);

        ScoreboardSnapshot snapshot = matchState.snapshot();

        assertEquals(1, snapshot.player1Sets());
        assertEquals(0, snapshot.player2Sets());
        assertEquals(0, snapshot.player1Games());
        assertEquals(0, snapshot.player2Games());
        assertEquals("0", snapshot.player1Points());
        assertEquals("0", snapshot.player2Points());
        assertFalse(snapshot.tieBreak());
    }

    @Test
    void shouldSwitchToTieBreakAtSixGamesAll() {
        reachSixGamesAll();

        ScoreboardSnapshot snapshot = matchState.snapshot();

        assertEquals(6, snapshot.player1Games());
        assertEquals(6, snapshot.player2Games());
        assertTrue(snapshot.tieBreak());
    }

    private void winMatch(WinnerSide winnerSide) {
        winSet(winnerSide);
        winSet(winnerSide);
    }

    private void winSet(WinnerSide winnerSide) {
        for (int game = 0; game < 6; game++) {
            winGame(winnerSide);
        }
    }

    private void winGame(WinnerSide winnerSide) {
        for (int point = 0; point < 4; point++) {
            matchState.updateScore(winnerSide);
        }
    }

    private void reachSixGamesAll() {
        for (int i = 0; i < 6; i++) {
            winGame(PLAYER_1);
            winGame(PLAYER_2);
        }
    }
}