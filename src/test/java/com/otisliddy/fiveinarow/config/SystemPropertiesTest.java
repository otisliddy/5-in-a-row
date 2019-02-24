package com.otisliddy.fiveinarow.config;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;

public class SystemPropertiesTest {

    @After
    public void clearSystemProperties() {
        System.clearProperty(SystemProperties.NUM_ROWS.getName());
    }

    @Test
    public void noValueProvidedDefaultValueReturned() {
        assertEquals(6, SystemProperties.NUM_ROWS.getValue());
    }

    @Test
    public void valueProvidedValueReturned() {
        final int expectedResult = 11;
        try{
            System.setProperty(SystemProperties.NUM_ROWS.getName(), String.valueOf(expectedResult));
            assertEquals(expectedResult, SystemProperties.NUM_ROWS.getValue());
        }
        finally{
            System.clearProperty(SystemProperties.NUM_ROWS.getName());
        }
    }
}
