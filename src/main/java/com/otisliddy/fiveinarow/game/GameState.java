package com.otisliddy.fiveinarow.game;

import java.awt.*;
import java.io.Serializable;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.otisliddy.fiveinarow.config.SystemProperties;
import com.otisliddy.fiveinarow.exception.IllegalMoveException;

/**
 * Holds the state of a game, including the grid of discs, the {@link GameStatus} and whose move it is.
 * <p>To external methods, columns are indexed starting at 1. Internally to the class, they are indexed starting at 0.</p>
 */
@Component
public class GameState implements Serializable {

    private static final int NUM_ROWS = SystemProperties.NUM_ROWS.getValue();
    private static final int NUM_COLUMNS = SystemProperties.NUM_COLUMNS.getValue();
    private static final int IN_A_ROW_TO_WIN = SystemProperties.IN_A_ROW_TO_WIN.getValue();
    private static final int EMPTY = 0;

    private int numRows;
    private int numColumns;
    private int inARow;

    /**
     * Outer array is the rows, inner array us the columns. (0,0) in the grid corresponds to the bottom left of the game as it should be displayed.
     */
    private int[][] grid;
    private Player playerToMove = null;
    private GameStatus status = GameStatus.IN_PROGRESS;

    /**
     * Construct an instance of {@code GameState}.
     *
     * @param numRows
     *            the number of rows to give to the grid of discs.
     * @param numColumns
     *            the number of columns to give to the grid of discs.
     * @param inARow
     *            the number of discs needed in-a-row to win.
     */
    public GameState(int numRows, int numColumns, int inARow) {
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.inARow = inARow;
        grid = new int[numRows][numColumns];
    }

    /**
     * Construct an instance of {@IllegalMove}. Empty constructor needed for injecting and for transfer over WebSockets.
     */
    public GameState() {
        this(NUM_ROWS, NUM_COLUMNS, IN_A_ROW_TO_WIN);
    }

    /**
     * Get the grid of discs.
     * <ul>
     * <li>Grid element '0' indicates no disc.</li>
     * <li>Grid element '1' indicates a disc for player with ID = 1.</li>
     * <li>Grid element '2' indicates a disc for player with ID = 2.</li>
     * </ul>
     *
     * @return the grid of discs.
     */
    public int[][] getGrid() {
        return grid;
    }

    /**
     * Set the grid of discs.
     *
     * @param grid
     *            the grid to set.
     */
    protected void setGrid(int[][] grid) {
        this.grid = grid;
    }

    /**
     * Add a disc to the provided column for the provided player ID.
     *
     * @param column
     *            the column to add the disc.
     * @param playerId
     *            the player ID who added the disc.
     * @throws IllegalMoveException
     *            if the provided column is not in range or the column is full of discs.
     */
    public void addDisc(int column, int playerId) throws IllegalMoveException {
        if (column < 1 || column > numColumns) {
            throw new IllegalMoveException(String.format("Column %d is out of range of (%d-%d)", column, 1, numColumns), playerId);
        }
        int newDiscRowIndex = getPopulatedHeight(column);
        if (newDiscRowIndex >= numRows) {
            throw new IllegalMoveException("No more discs may be added to column " + column + " because it is full", playerId);
        }
        int newDiscRow = getPopulatedHeight(column);
        grid[newDiscRow][column - 1] = playerId;

        calculateNewGameState(newDiscRow, column - 1, playerId);
    }

    private int getPopulatedHeight(int column) {
        int result = 0;
        while (result < numRows && grid[result][column - 1] != EMPTY) {
            result++;
        }
        return result;
    }

    private void calculateNewGameState(int rowOfNewDisc, int columnOfNewDisc, int playerId) {
        for (Direction direction : Direction.values()) {
            if (isFiveInARow(rowOfNewDisc, columnOfNewDisc, direction, playerId)) {
                status = GameStatus.OVER_WON;
                return;
            }
        }
        if (isDraw()) {
            status = GameStatus.OVER_DRAWN;
        }
    }

    private boolean isFiveInARow(int rowOfNewDisc, int columnOfNewDisc, final Direction direction, final int playerId) {
        final Point position = new Point(rowOfNewDisc, columnOfNewDisc);
        for (int i = 0; i < inARow; i++) {
            if (!gridPositionExists(position) || grid[position.x][position.y] != playerId) {
                return false;
            }
            position.translate(direction.horizontal, direction.vertical);
        }
        return true;
    }

    private boolean gridPositionExists(final Point position) {
        return position.x >= 0 && position.x < numRows && position.y >= 0 && position.y < numColumns;
    }

    private boolean isDraw() {
        for (int i = 0; i < numColumns; i++) {
            if (grid[numRows - 1][i] == EMPTY) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the player whose move it is next.
     *
     * @return the player whose move it is next. May be {@code null}, in which case no player has yet been set to move.
     */
    @Nullable
    public Player getPlayerToMove() {
        return playerToMove;
    }

    /**
     * Set the player whose move it is next.
     *
     * @param player
     *            the player whose move it is next.
     */
    public void setPlayerToMove(Player player) {
        this.playerToMove = player;
    }

    /**
     * Retrieve the {@link GameStatus} of this game.
     *
     * @return the {@link GameStatus} of this game.
     */
    public GameStatus getStatus() {
        return status;
    }

    /**
     * Set the {@link GameStatus} of this game.
     *
     * @param status
     *            the {@link GameStatus} of this game.
     */
    public void setStatus(final GameStatus status) {
        this.status = status;
    }

    /**
     * Directions relative to a position in the game grid.
     */
    private enum Direction {
        UP_RIGHT(1, 1), RIGHT(0, 1), DOWN_RIGHT(-1, 1), DOWN(-1, 0), DOWN_LEFT(-1, -1), LEFT(0, -1), UP_LEFT(1, -1);

        int horizontal;
        int vertical;

        Direction(final int horizontal, final int vertical) {
            this.horizontal = horizontal;
            this.vertical = vertical;
        }
    }
}
