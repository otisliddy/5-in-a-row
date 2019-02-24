package com.otisliddy.fiveinarow;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.otisliddy.fiveinarow.config.SystemProperties;

@SpringBootApplication
public class Application {

    /**
     * Main class, delegates to Spring Boot.
     *
     * @param args
     *            program arguments.
     */
    public static void main(String[] args) {
        final SpringApplication fiveinarow = new SpringApplication(Application.class);
        fiveinarow.setDefaultProperties(Collections.singletonMap("server.port", String.valueOf(SystemProperties.PORT.getValue())));
        fiveinarow.run(args);
    }

}
