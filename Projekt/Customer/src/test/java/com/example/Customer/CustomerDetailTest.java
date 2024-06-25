package com.example.Customer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerDetailTest {

    // Test the setter and getter methods of the CustomerDetail class
    @Test
    public void testCustomerDetailSettersAndGetters() {
        CustomerDetail detail = new CustomerDetail();

        // Set values using setter methods
        detail.setFirstName("John");
        detail.setLastName("Doe");
        detail.setId(1);
        detail.setTotalCharge(100.0);

        // Assert that the getter methods return the correct values
        assertEquals("John", detail.getFirstName());
        assertEquals("Doe", detail.getLastName());
        assertEquals(1, detail.getId());
        assertEquals(100.0, detail.getTotalCharge());
    }
}