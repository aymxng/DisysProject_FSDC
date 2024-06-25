package com.example.Customer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*; // Imports classes for handling SQL operations

/**
 * This file defines a Spring Boot REST controller which handles HTTP requests related to charging
 * stations. The controller provides an endpoint to get the total amount of energy charged by a specific
 * customer. It includes a method to establish a database connection and a method to process GET
 * requests for fetching the total charge of a customer from the database.
 */

@RestController // Marks this class as a REST controller, making it capable of handling HTTP requests
@RequestMapping("/station") // Maps HTTP requests to /station to methods in this controller
public class StationOneController {

    // Establishes a connection to the PostgreSQL database using the provided URL, username, and password
    private Connection dbConnectionObject() throws SQLException {
        String dataBaseUrl = "jdbc:postgresql://localhost:5432/station1"; // URL
        String databaseRootUser = "postgres"; // username
        String databasePassword = "admin"; // password

        // Returns a connection object
        return DriverManager.getConnection(dataBaseUrl + "?user=" + databaseRootUser + "&password=" + databasePassword);
    }

    // Handles GET requests to /station/customer to get the total charge of a specific customer
    @GetMapping("/customer")
    public Double totalChargedByCustomerId(@RequestParam("customerId") Integer customerId) {

        try {
            Connection connection = dbConnectionObject(); // Establishes a database connection
            // SQL query to get the total charge for a customer
            String sql = "SELECT sum(kwh) as totalCharge FROM charge where customer_id=" + customerId;
            // Prepared statement
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // Executes the query and gets the result set
            ResultSet resultSet = preparedStatement.executeQuery();
            // Checks if the result set has a result
            if (resultSet.next()) {
                return resultSet.getDouble("totalCharge"); // if yes: returns total charge
            }
          // Catches any exceptions that occur during the process
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0.0; // if no result or exception return 0.0
    }

}
