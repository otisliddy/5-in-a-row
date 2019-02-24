package com.otisliddy.fiveinarow.exception;

/**
 * Exception for a an illegal move.
 *
 * @see IllegalMove
 */
public class IllegalMoveException extends Exception {

    private IllegalMove illegalMove;

    /**
     * Construct an instance with the provided message.
     *
     * @param messgae
     *            the error message.
     * @param playerId
     *            the ID of the player associated with the illefgal move.
     */
    public IllegalMoveException(String messgae, int playerId) {
        super(messgae);
        illegalMove = new IllegalMove(messgae, playerId);
    }

    /**
     * Get the {@code IllegalMove}.
     *
     * @return the {@code IllegalMove}.
     */
    public IllegalMove getIllegalMove() {
        return illegalMove;
    }
}
