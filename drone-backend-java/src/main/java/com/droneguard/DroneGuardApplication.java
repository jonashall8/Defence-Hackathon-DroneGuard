package com.droneguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DroneGuardApplication {

    public static void main(String[] args) {
        // This line starts the embedded Tomcat server on port 8080
        SpringApplication.run(DroneGuardApplication.class, args);
    }

}