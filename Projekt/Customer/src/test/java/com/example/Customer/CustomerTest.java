package com.example.Customer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {

    // Test the getter methods of the Customer class
    @Test
    public void testCustomerGetters() {
        Customer customer = new Customer();

        // Using reflection to set private fields for testing purposes
        // Alternatively, you can add a constructor to the Customer class
        java.lang.reflect.Field field;
        try {
            field = Customer.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(customer, 1);

            field = Customer.class.getDeclaredField("firstName");
            field.setAccessible(true);
            field.set(customer, "John");

            field = Customer.class.getDeclaredField("lastName");
            field.setAccessible(true);
            field.set(customer, "Doe");
        } catch (Exception e) {
            fail("Reflection failed to set fields");
        }

        // Assert that the getter methods return the correct values
        assertEquals(1, customer.getId());
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
    }
}