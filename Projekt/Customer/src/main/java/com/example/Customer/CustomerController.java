package com.example.Customer;

// Import statements for necessary libraries
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
 * Handles HTTP requests for customer data and invoice generation.
 * Provides endpoints for generating and downloading PDF invoices.
 *
 * Usage:
 * - Handles GET and POST requests for customer data and invoices.
 *
 * Interaction:
 * - Fetches data from Customer entity and external APIs.
 * - Uses CustomerDetail for detailed data handling.
 * - Generates PDF invoices using iText library.
 */


//Indicates a RESTful web service Controller and map the request to /invoice
@RestController
@RequestMapping("/invoice")
public class CustomerController {


    // Create a connection to our customer database with the defined url,name and password
    private Connection dbConnectionObject() throws SQLException {

        String dbUrl= "jdbc:postgresql://localhost:5432/customer";

        String dbUser="postgres";

        String dbPass="admin";

        // Establish and return the database connection
        return DriverManager.getConnection(dbUrl + "?user=" + dbUser + "&password=" + dbPass);
    }

    /*
     * Handles HTTP GET requests to generate data for a specific customer ID.
     * Fetches customer data from the database and calls an external API to gather additional information.
     */
    @GetMapping("/{customerId}")
    public String generatingData(@PathVariable("customerId") Integer customerId)  {

        //Check if customer id is not null, establish connection, prepares and execture SQL query to get customer data based on ID
        if (customerId != null) {
            try {
                Connection connection = dbConnectionObject();
                String sql = "SELECT * FROM customer WHERE id=" + customerId;
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                // Execute query to fetch data, if customer exists create a URL call to the API for additional data

                // Return messages based on the results
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String url = "http://localhost:8081/stations/" + customerId;
                    String totalCharge = callApi(url);
                    if (totalCharge != null) {

                        return "Data Collection presses started " + totalCharge;
                    }
                    return "Collection presses started";
                } else {
                    return "No Customer found with this id : "+customerId;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return "something went wrong";
            }

        }
        return "No Customer data found";
    }

    // Call API using the provided URL and return response as a string
    private String callApi(String url) throws IOException {
        // Create a URL object from the given URL string
        URL urlObj = new URL(url);
        // Open a connection to the URL
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        // Set the request method to GET
        connection.setRequestMethod("GET");

        // Check if response code is not 200 (OK), return null if so
        try {
            if (connection.getResponseCode() != 200) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        // Read the response from the input stream
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        // Use a StringBuilder to create the response
        StringBuilder responseBuilder = new StringBuilder();
        String line;

        // Read each line from the BufferedReader and append it to the StringBuilder
        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line).append("\n");
        }
        reader.close();
        // Convert the accumulated response to a string and return it
        return responseBuilder.toString();
    }

    // Generate and download a PDF based on the gathered data for an invoice for a specific customer, return it as a byte array in the HTTP response.
    @PostMapping("/{customerId}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable String customerId) throws IOException {

        // Create a new CustomerDetail object to hold customer data
        CustomerDetail detail = new CustomerDetail();

        // Establish database connection, prepare and execute SQL query to fetch specific data for the customerId
        try {
            Connection connection = dbConnectionObject();
            String sql = "SELECT * FROM customer WHERE id=" + customerId;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();
            // Check if the customer exists in the result set
            if (resultSet.next()) {
                // Construct the URL to call the external API using the customer ID
                String url = "http://localhost:8081/stations/" + customerId;
                // Call the external API and get the total charge
                String totalCharge = callApi(url);
                // If the total charge is not null, populate the CustomerDetail object with data
                if (totalCharge != null) {
                    detail.setId(Integer.valueOf(customerId));
                    detail.setFirstName(resultSet.getString("first_name"));
                    detail.setLastName(resultSet.getString("last_name"));
                    detail.setTotalCharge(Double.valueOf(totalCharge));
                }

            }
            // Print error message if an error occurs
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }

        // Generate the PDF invoice based on the customer details
        byte[] pdfBytes = generateInvoice(detail);

        // Return (200 ok) the PDF as a byte array in the HTTP response with appropriate headers
        return ResponseEntity.ok()
                //Set the Content-Disposition header to indicate that the response should be treated as an attachment
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + customerId + ".pdf")
                //Indicate that repsonse is a PDF File
                .contentType(MediaType.APPLICATION_PDF)
                // Set body of the response to the pdfBytes byte array, which contains the generated PDF content
                .body(pdfBytes);

    }

    /*
     * Generates a PDF invoice based on the provided CustomerDetail object.
     * Uses the iText library to create a PDF document and returns it as a byte array.
     */

    public byte[] generateInvoice(CustomerDetail detail) throws IOException {

        // Creates the necessary objects to generate a PDF document using the iText library.
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

// Create a bold and a regular font using helvetica font style
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

// Add the information to the PDF (Company name in bold) adress and phone regular font
        document.add(new Paragraph("[Greenie]")
                .setFont(bold)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.LEFT));

        document.add(new Paragraph("[Green Street]\nPhone: 06765512342")
                .setFont(regular)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.LEFT));

        // Create and add table with cells(helper methods) with invoice details to the PDF document
        Table invoiceDetails = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth();

        invoiceDetails.addCell(createCell("INVOICE", bold, 20, TextAlignment.RIGHT, 2, ColorConstants.LIGHT_GRAY));
        invoiceDetails.addCell(createCell("INVOICE Nr", bold, 10, TextAlignment.RIGHT, ColorConstants.LIGHT_GRAY));
        invoiceDetails.addCell(createCell("", regular, 10, TextAlignment.RIGHT));
        invoiceDetails.addCell(createCell("Date", bold, 10, TextAlignment.RIGHT, ColorConstants.LIGHT_GRAY));
        invoiceDetails.addCell(createCell(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyy")), regular, 10, TextAlignment.RIGHT));

        document.add(invoiceDetails);

        // Add a new parapraph followed by  billing information such as customers full name and ID
        document.add(new Paragraph("\n"));


        document.add(new Paragraph("Customer Billing Info")
                .setFont(bold)
                .setFontSize(15)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.LEFT));
        document.add(new Paragraph(detail.getFirstName() + " " + detail.getLastName() + "\n(Customer Id) : " + detail.getId())
                .setFont(regular)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.LEFT));

        // Add table which holds the description of charges
        Table descriptionTable = new Table(UnitValue.createPercentArray(new float[]{4, 1}))
                .useAllAvailableWidth();

        descriptionTable.addHeaderCell(createCell("DESCRIPTION", bold, 10, TextAlignment.LEFT, ColorConstants.LIGHT_GRAY));
        descriptionTable.addHeaderCell(createCell("-", bold, 10, TextAlignment.RIGHT, ColorConstants.LIGHT_GRAY));

        descriptionTable.addCell(createCell("Total charged by the customer  kwh", regular, 10, TextAlignment.LEFT));
        descriptionTable.addCell(createCell(detail.getTotalCharge().toString(), regular, 10, TextAlignment.RIGHT));


        document.add(descriptionTable);


        document.add(new Paragraph("\n"));

// Create and add table with the total payment information like thank you message, total label, total payment, discount, and the final amount after discount
        Table totalTable = new Table(UnitValue.createPercentArray(new float[]{4, 1}))
                .useAllAvailableWidth();
        totalTable.addCell(createCell("Thank you!", bold, 10, TextAlignment.LEFT, 1, ColorConstants.LIGHT_GRAY));
        totalTable.addCell(createCell("TOTAL", bold, 10, TextAlignment.RIGHT, ColorConstants.LIGHT_GRAY));
        totalTable.addCell(createCell("Total Payment (10 $/kwh) ", bold, 10, TextAlignment.LEFT));
        totalTable.addCell(createCell("Discount (4.5$)", bold, 10, TextAlignment.LEFT));
        totalTable.addCell(createCell(String.valueOf((detail.getTotalCharge() * 10) - 4.5 ), bold, 10, TextAlignment.RIGHT));

        document.add(totalTable);


        document.add(new Paragraph("\n"));

// Create parapgraph for asking questions
        document.add(new Paragraph("If you have any questions about this invoice, please contact\n[Greenie, 06765512342, greenie@email.com]")
                .setFont(regular)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));


        // Close the PDF document to finalize it and return the generated PDF as a byte array
        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    // Methods to create table cells with different levels of customization with specified content, font, font size, alignment, and optional background color and colspan.
    private Cell createCell(String content, PdfFont font, int fontSize, TextAlignment alignment) {
        return new Cell()
                .add(new Paragraph(content)
                        .setFont(font)
                        .setFontSize(fontSize))
                .setTextAlignment(alignment)
                .setPadding(8);
    }

    private Cell createCell(String content, PdfFont font, int fontSize, TextAlignment alignment, Color backgroundColor) {
        return new Cell()
                .add(new Paragraph(content)
                        .setFont(font)
                        .setFontSize(fontSize))
                .setTextAlignment(alignment)
                .setBackgroundColor(backgroundColor)
                .setPadding(8);
    }

    private Cell createCell(String content, PdfFont font, int fontSize, TextAlignment alignment, int colspan, Color backgroundColor) {
        Cell cell = new Cell(1, colspan)
                .add(new Paragraph(content)
                        .setFont(font)
                        .setFontSize(fontSize))
                .setTextAlignment(alignment)
                .setBackgroundColor(backgroundColor)
                .setPadding(8);
        return cell;
    }


}