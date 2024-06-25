package com.example.Customer;

/*
 * Represents detailed customer information, including total charge.
 * Used as a Data Transfer Object (DTO) for invoice generation.
 *
 * Usage:
 * - For generating invoices with comprehensive customer data.
 *
 * Interaction:
 * - Populated by CustomerController with data from Customer and other sources.
 */

public class CustomerDetail {

    // Initialize the customers information
    private String firstName;

    private String lastName;

    private Integer id;

    private Double totalCharge;

    //Getter and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(Double totalCharge) {
        this.totalCharge = totalCharge;
    }
}
