package com.otisliddy.fiveinarow.exception;

import java.io.Serializable;

/**
 * POJO representing qan illegal game move.
 */
public class IllegalMove implements Serializable {

    private String message;
    private int playerId;

    /**
     * Construct an instance. Empty constructor is needed for transfer over WebSockets.
     */
    public IllegalMove() {
    }

    /**
     * Construct an instance with the provided arguments,
     *
     * @param message
     *            the message describing the illegal move.
     * @param playerId
     *            the ID of the player who made the illegal move.
     */
    public IllegalMove(String message, int playerId) {
        this.message = message;
        this.playerId = playerId;
    }

    /**
     * Get the illegal move message.
     *
     * @return the illegal move message,
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the ID of the player associated with the illegal move.
     *
     * @return the ID of the player associated with the illegal move.
     */
    public int getPlayerId() {
        return playerId;
    }
}