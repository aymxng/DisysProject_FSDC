package com.example.Customer;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/*
 * Represents basic customer for storing and retrieving basic customer data.
 *
 * Usage:
 * - For CRUD operations in the database (Annotations).
 *
 * Interaction:
 * - Works with CustomerController to fetch and manage customer data.
 */
@Entity
public class Customer {

    @Id
    private int id;

    private String firstName;

    private String lastName;

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
