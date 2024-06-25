package com.example.Customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // Marks this class as the main entry point for the Spring Boot application and enables auto-configuration
public class FirstStationApplication {

	// Launches the Spring Boot application
	public static void main(String[] args) {
		SpringApplication.run(FirstStationApplication.class, args);
	}

}
