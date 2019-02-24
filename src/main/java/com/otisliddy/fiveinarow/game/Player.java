package com.otisliddy.fiveinarow.game;

import java.io.Serializable;
import java.util.Objects;

/**
 * POJO for a game player
 */
public class Player implements Serializable {

    private String name;

    private int id;

    /**
     * Construct an instance of a player.
     *
     * @param name
     *            the player name.
     * @param id
     *            the player ID.
     */
    public Player(String name, int id) {
        this.name = name;
        this.id = id;
    }

    /**
     * Construct an instance of a player. Empty constructor is needed for transfer over WebSockets.
     */
    public Player() {
    }

    /**
     * Get the player's name.
     *
     * @return the player name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the player ID.
     *
     * @return the player ID.
     */
    public int getId() {
        return id;
    }

    @Override
    public boolean equals(final Object otherPlayer) {
        if (this == otherPlayer)
            return true;
        if (otherPlayer == null || getClass() != otherPlayer.getClass())
            return false;
        final Player player = (Player) otherPlayer;
        return id == player.id && Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }

    @Override
    public String toString() {
        return String.format("Player attributes: name=%s, id=%s", name, id);
    }
}
