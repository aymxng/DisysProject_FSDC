package com.example.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;



@RestController
@RequestMapping("/stations")
public class StationHubControl {

    private final String databaseConnUrl = "jdbc:postgresql://localhost:5432/station";

    private String dataBaseUSer = "postgres";

    private String dbUserPassword = "admin";

    @Autowired
    private Producer producer;

    @Autowired
    private Consumer consumer;


    @GetMapping("/{customerId}")
    public Double generateRepostForCustomer(@PathVariable("customerId") String customerId) {
        Double totalCharges = 0.0;
        try {
            Connection connection = databaseConnectionMethod();
            String sql = "SELECT * FROM station_hub";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String url = "http://" + resultSet.getString("db_url") + "/station/customer?customerId=" + customerId;
                String totalCharge = callApi(url);
                if (totalCharge != null) {
                    totalCharges = Double.parseDouble(totalCharge) + totalCharges;
                    producer.sendMessage(totalCharge);
                }

            }
            return totalCharges;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        consumer.processMessage(String.valueOf(totalCharges));
        return 0.0;
    }


    private String callApi(String url) throws IOException {
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod("GET");
        try {
            if (connection.getResponseCode() != 200) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line).append("\n");
        }
        reader.close();

        return responseBuilder.toString();
    }


    private Connection databaseConnectionMethod() throws SQLException {
        return DriverManager.getConnection(databaseConnUrl + "?user=" + dataBaseUSer + "&password=" + dbUserPassword);
    }

}
