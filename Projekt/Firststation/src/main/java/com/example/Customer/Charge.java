package com.example.Customer;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;


@Entity // Marks this class as a JPA entity, indicating that it will be mapped to a table in the database
public class Charge {

    @Id // Specifies the primary key of the entity, ensuring each charge record is uniquely identifiable
    private int id;
    private Double kwh; // Stores the amount of energy consumed in the charge transaction
    private Integer customerId; // Stores the ID of the customer associated with this charge transaction
}


