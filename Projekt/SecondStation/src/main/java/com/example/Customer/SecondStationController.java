package com.example.Customer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;


@RestController // Marks this class as a REST controller, making it capable of handling HTTP requests
@RequestMapping("/station") // Maps HTTP requests to /station to methods in this controller
public class SecondStationController {

    // Establishes a connection to the PostgreSQL database using the provided URL, username, and password
    private Connection dbConnect() throws SQLException {
        String dataBaseConnUrl = "jdbc:postgresql://localhost:5432/station2"; // url
        String dataBaseUser = "postgres"; // username
        String dataBaseUserPassword = "admin"; // password

        // Returns a connection object
        return DriverManager.getConnection(dataBaseConnUrl + "?user=" + dataBaseUser + "&password=" + dataBaseUserPassword);
    }

    // Handles GET requests to /station/customer to get the total charge of a specific customer
    @GetMapping("/customer")
    public Double totalChargedByCustomerId(@RequestParam("customerId") Integer customerId) {

        try {
            Connection connection = dbConnect(); // Establishes a database connection
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
            e.printStackTrace(); // // Prints the stack trace of the exception (for detailed debugging information)
            return 0.0;
        }
        return 0.0; // if no result or exception return 0.0
    }

}
