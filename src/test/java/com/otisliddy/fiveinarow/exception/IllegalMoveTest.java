package com.otisliddy.fiveinarow.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class IllegalMoveTest {

    @Test
    public void createWithArgs() {
        final IllegalMove illegalMove = new IllegalMove("message", 12);
        assertEquals("message", illegalMove.getMessage());
        assertEquals(12, illegalMove.getPlayerId());
    }

    @Test
    public void createWithoutArgs() {
        final IllegalMove illegalMove = new IllegalMove();
        assertNull(illegalMove.getMessage());
        assertEquals(0, illegalMove.getPlayerId());
    }

}
