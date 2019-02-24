package com.otisliddy.fiveinarow.config;

/**
 * This class defines the system properties that can be used when invoking 5-in-a-row as a program.
 */
public enum SystemProperties {

    /**
     * The number of rows in the game
     */
    NUM_ROWS("inarow.numrows", "6"),

    /**
     * The number of columns in the game
     */
    NUM_COLUMNS("inarow.numcols", "9"),

    /**
     * The number of discs needed, in a row, to win the game. The direction of the discs may be horizontal, vertical or diagonal.
     */
    IN_A_ROW_TO_WIN("inarow.inarow", "5"),

    /**
     * The number of discs needed, in a row, to win the game. The direction of the discs may be horizontal, vertical or diagonal.
     */
    PORT("inarow.port", "8080");

    private final String name;
    private final String value;

    SystemProperties(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Get the name of this property
     *
     * @return the name of this property
     */
    public String getName() {
        return name;
    }

    /**
     * Get the inetegr value for this property.
     *
     * @return the integer value for this property. If no System Property exits for the given property, then the default value is returned.
     */
    public int getValue() {
        return Integer.parseInt(System.getProperty(name, value));
    }

}
