package com.otisliddy.fiveinarow.game;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.otisliddy.fiveinarow.exception.IllegalMoveException;

public class GameStateTest {

    private GameState game = new GameState(3, 3, 3);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void addDisc() {
        int[][] initialGrid = { { 0, 1, 1 }, { 0, 0, 2 }, { 0, 0, 0 } };
        game.setGrid(initialGrid);

        addDisc(1, 1);
        addDisc(2, 1);
        addDisc(3, 2);

        int[][] expected = { { 1, 1, 1 }, { 0, 1, 2 }, { 0, 0, 2 } };
        assertArrayEquals(expected, game.getGrid());
    }

    @Test
    public void addDiscToFullColumn() throws IllegalMoveException {
        int[][] initialGrid = { { 0, 1, 1 }, { 0, 0, 2 }, { 0, 0, 2 } };
        game.setGrid(initialGrid);

        expectedException.expect(IllegalMoveException.class);
        expectedException.expectMessage("No more discs may be added to column 3");

        game.addDisc(3, 1);
    }

    @Test
    public void addDiscToFullColumn_moreColumnsThanRows() throws IllegalMoveException {
        game = new GameState(2, 3, 7);
        int[][] initialGrid = { { 1, 1, 1 }, { 2, 0, 2 } };
        game.setGrid(initialGrid);

        expectedException.expect(IllegalMoveException.class);
        expectedException.expectMessage("No more discs may be added to column 1");

        game.addDisc(1, 2);
    }

    @Test
    public void addDiscToFullColumn_moreRowsThanColumns() throws IllegalMoveException {
        game = new GameState(3, 2, 7);
        int[][] initialGrid = { { 1, 1 }, { 1, 2 }, { 0, 2 } };
        game.setGrid(initialGrid);

        expectedException.expect(IllegalMoveException.class);
        expectedException.expectMessage("No more discs may be added to column 2");

        game.addDisc(2, 2);
    }

    @Test
    public void addDiscToIllegalColumn_tooLow() throws IllegalMoveException {
        expectedException.expect(IllegalMoveException.class);
        expectedException.expectMessage("Column 0 is out of range of (1-3)");

        game.addDisc(0, 1);
    }

    @Test
    public void addDiscToIllegalColumn_tooHigh() throws IllegalMoveException {
        expectedException.expect(IllegalMoveException.class);
        expectedException.expectMessage("Column 4 is out of range of (1-3)");

        game.addDisc(4, 1);
    }

    @Test
    public void winnerTopRight() {
        int[][] initialGrid = { { 0, 2, 2 }, { 0, 1, 2 }, { 0, 0, 1 } };
        game.setGrid(initialGrid);

        addDisc(1, 1);

        assertEquals(GameStatus.OVER_WON, game.getStatus());
    }

    @Test
    public void notWinnerTopRight() {
        int[][] initialGrid = { { 0, 2, 1 }, { 0, 1, 2 }, { 0, 0, 2 } };
        game.setGrid(initialGrid);

        addDisc(1, 1);

        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    public void winnerRight() {
        int[][] initialGrid = { { 1, 2, 2 }, { 0, 1, 1 }, { 0, 0, 0 } };
        game.setGrid(initialGrid);

        addDisc(1, 1);

        assertEquals(GameStatus.OVER_WON, game.getStatus());
    }

    @Test
    public void notWinnerRight() {
        int[][] initialGrid = { { 1, 2, 2 }, { 0, 1, 2 }, { 0, 2, 2 } };
        game.setGrid(initialGrid);

        addDisc(1, 1);

        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    public void winnerDownRight() {
        int[][] initialGrid = { { 2, 2, 1 }, { 2, 1, 0 }, { 0, 0, 1 } };
        game.setGrid(initialGrid);

        addDisc(1, 1);

        assertEquals(GameStatus.OVER_WON, game.getStatus());
    }

    @Test
    public void notWinnerDownRight() {
        int[][] initialGrid = { { 1, 1, 2 }, { 2, 1, 0 }, { 0, 0, 0 } };
        game.setGrid(initialGrid);

        addDisc(1, 1);

        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    public void winnerDown() {
        int[][] initialGrid = { { 0, 0, 1 }, { 0, 0, 1 }, { 0, 0, 0 } };
        game.setGrid(initialGrid);

        addDisc(3, 1);

        assertEquals(GameStatus.OVER_WON, game.getStatus());
    }

    @Test
    public void notWinnerDown() {
        int[][] initialGrid = { { 0, 0, 2 }, { 0, 0, 1 }, { 0, 0, 0 } };
        game.setGrid(initialGrid);

        addDisc(1, 1);

        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    public void winnerDownLeft() {
        int[][] initialGrid = { { 1, 2, 2 }, { 0, 1, 2 }, { 0, 0, 0 } };
        game.setGrid(initialGrid);

        addDisc(3, 1);

        assertEquals(GameStatus.OVER_WON, game.getStatus());
    }

    @Test
    public void notWinnerDownLeft() {
        int[][] initialGrid = { { 2, 1, 2 }, { 0, 1, 2 }, { 0, 0, 0 } };
        game.setGrid(initialGrid);

        addDisc(3, 1);

        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    public void winnerLeft() {
        int[][] initialGrid = { { 1, 2, 2 }, { 1, 1, 0 }, { 0, 0, 0 } };
        game.setGrid(initialGrid);

        addDisc(1, 1);

        assertEquals(GameStatus.OVER_WON, game.getStatus());
    }

    @Test
    public void notWinnerLeft() {
        int[][] initialGrid = { { 1, 2, 2 }, { 2, 1, 0 }, { 0, 0, 0 } };
        game.setGrid(initialGrid);

        addDisc(3, 1);

        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    public void winnerUpLeft() {
        int[][] initialGrid = { { 1, 2, 0 }, { 2, 1, 0 }, { 1, 0, 0 } };
        game.setGrid(initialGrid);

        addDisc(3, 1);

        assertEquals(GameStatus.OVER_WON, game.getStatus());
    }

    @Test
    public void notWinnerUpLeft() {
        int[][] initialGrid = { { 1, 2, 0 }, { 2, 1, 0 }, { 2, 0, 0 } };
        game.setGrid(initialGrid);

        addDisc(3, 1);

        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    public void drawLeftColumn() {
        game = new GameState(2, 3, 3);
        int[][] initialGrid = { { 1, 2, 1 }, { 0, 1, 2 } };
        game.setGrid(initialGrid);

        addDisc(1, 1);

        assertEquals(GameStatus.OVER_DRAWN, game.getStatus());
    }

    @Test
    public void notDrawLeftColumn() {
        game = new GameState(2, 3, 3);
        int[][] initialGrid = { { 1, 2, 1 }, { 0, 1, 0 } };
        game.setGrid(initialGrid);

        addDisc(1, 1);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    public void drawMiddleColumn() {
        game = new GameState(2, 3, 3);
        int[][] initialGrid = { { 1, 2, 1 }, { 2, 0, 2 } };
        game.setGrid(initialGrid);

        addDisc(2, 1);

        assertEquals(GameStatus.OVER_DRAWN, game.getStatus());
    }

    @Test
    public void notDrawMiddleColumn() {
        game = new GameState(2, 3, 3);
        int[][] initialGrid = { { 1, 2, 1 }, { 2, 0, 0 } };
        game.setGrid(initialGrid);

        addDisc(2, 1);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    public void drawRightColumn() {
        game = new GameState(2, 3, 3);
        int[][] initialGrid = { { 1, 2, 1 }, { 2, 1, 0 } };
        game.setGrid(initialGrid);

        addDisc(3, 1);

        assertEquals(GameStatus.OVER_DRAWN, game.getStatus());
    }

    @Test
    public void notDrawRightColumn() {
        game = new GameState(2, 3, 3);
        int[][] initialGrid = { { 1, 2, 1 }, { 0, 1, 0 } };
        game.setGrid(initialGrid);

        addDisc(3, 1);

        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    public void fullGameLargeGrid() {
        game = new GameState(3, 6, 3);

        addDisc(3, 1);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        addDisc(4, 2);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        addDisc(4, 1);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        addDisc(3, 2);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        addDisc(6, 1);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        addDisc(5, 2);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        addDisc(1, 1);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        addDisc(2, 2);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        addDisc(2, 1);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        addDisc(3, 2);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        addDisc(1, 1);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        addDisc(1, 2);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        addDisc(5, 1);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        addDisc(6, 2);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        addDisc(2, 1);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        addDisc(4, 2);
        assertEquals(GameStatus.OVER_WON, game.getStatus());
    }

    private void addDisc(int column, int playerId) {
        try {
            game.addDisc(column, playerId);
        } catch (IllegalMoveException exception) {
            exception.printStackTrace();
            fail(exception.getMessage());
        }
    }

}
