package com.otisliddy.fiveinarow.game;

import java.io.Serializable;

/**
 * Represents the progress of a game of 5-in-a-row at a given moment,
 */
public enum GameStatus implements Serializable {

    /**
     * The game has been started but has not been completed.
     */
    IN_PROGRESS,

    /**
     * The game has been won by a player getting 5 in a row.
     */
    OVER_WON,

    /**
     * The game has been drawn by every grid position being used without either player getting 5 in a row.
     */
    OVER_DRAWN
}
