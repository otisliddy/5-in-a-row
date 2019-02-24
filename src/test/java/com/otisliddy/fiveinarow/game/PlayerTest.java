package com.otisliddy.fiveinarow.game;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class PlayerTest {

    @Test
    public void createWithArgs() {
        final Player player = new Player("player name", 123);
        assertEquals("player name", player.getName());
        assertEquals(123, player.getId());
    }

    @Test
    public void createWithoutArgs() {
        final Player player = new Player();
        assertNull(player.getName());
        assertEquals(0, player.getId());
    }

    @Test
    public void equalsOfPlayersWithSameAttributes() {
        final Player playerA = new Player("player name", 123);
        final Player playerB = new Player("player name", 123);
        assertTrue(playerA.equals(playerB));
    }

    @Test
    public void testToString() {
        final Player player = new Player("player name", 123);
        assertEquals("Player attributes: name=player name, id=123", player.toString());
    }

}
