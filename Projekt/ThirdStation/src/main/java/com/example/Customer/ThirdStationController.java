package com.example.Customer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;



@RestController
@RequestMapping("/station")
public class ThirdStationController {

    private Connection dbConnect() throws SQLException {
        String dbUrl = "jdbc:postgresql://localhost:5432/station3";

        String dbUser = "postgres";

        String dbPass = "admin";

        return DriverManager.getConnection(dbUrl + "?user=" + dbUser + "&password=" + dbPass);
    }

    @GetMapping("/customer")
    public Double totalChargedByCustomerId(@RequestParam("customerId") Integer customerId) {

        try {
            Connection connection = dbConnect();
            String sql = "SELECT sum(kwh) as totalCharge FROM charge where customer_id=" + customerId;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("totalCharge");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
        return 0.0;
    }

}
