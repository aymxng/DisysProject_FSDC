package at.technikum.javafx;
// This class uses Fxml Annotations to link its methods with the UI elements defined in Customer.fxml


// Import the necessary classes for JavaFX and other libraries for handling the UI,file operations and networking
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OrderController {

    // Set a constant for the base URL of the customer API
    private static final String CUSTOMER_URL = "http://localhost:8080/invoice/";

    // Use FXML annotations to link the fields to the corresponding UI elements
    @FXML
    private TextField customerId;

    @FXML
    private Label responseLabel;

    @FXML
    private Button downloadButton;

    // Define method generateIfPdfIfCustomerDataExist which handles the download of the invoice based on customer ID
    public void generateIfPdfIfCustomerDataExist(ActionEvent actionEvent) throws URISyntaxException {

        // Initially hide the download button
        downloadButton.setVisible(false);

        // Retrieve the entered customer ID from the text field and build an HTTP POST request with it
        String customer = customerId.getText();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(CUSTOMER_URL + customer))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        // Sends an asynchronous HTTP request, handles the response by saving it as a PDF in the user's Downloads directory, and updates the UI accordingly.
        HttpClient.newBuilder()
                .build()
                // Async request, handle response as byte array
                .sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                // Extract response body
                .thenApply(HttpResponse::body)
                // Define what to do with the response body
                .thenAccept(response -> {
                    // Get the user's home directory
                    String homeDir = System.getProperty("user.home");
                    // Create a Path object for the Downloads directory
                    Path targetDir = Paths.get(homeDir, "Downloads");
                    // Check if the Downloads directory exists if not create one
                    if (!Files.exists(targetDir)) {
                        try {
                            // if non-existing create new download directory
                            Files.createDirectories(targetDir);
                        } catch (IOException e) {
                            Platform.runLater(() -> {
                                // Show error message if necessary
                                responseLabel.setText("Error creating target directory: " + e.getMessage());
                            });
                            return;
                        }
                    }

                    // Create the filename for the PDF and resolve the path
                    String filename = "invoice_" + customer +"_"+ (System.nanoTime()/ 1000) + ".pdf";
                    Path filePath = targetDir.resolve(filename);

                    // Write the response into a file
                    try (FileOutputStream outputStream = new FileOutputStream(filePath.toFile())) {
                        outputStream.write(response); // Write the PDF content (byte array) to the file

                        // Update the UI on the JavaFX Application from background thread
                        Platform.runLater(() -> {
                            responseLabel.setText("PDF downloaded successfully on given location: " + filePath);
                            customerId.setText(""); // Clear the customerId text field

                        });
                      // Handle any IO exceptions that occur during file writing
                    } catch (IOException e) {
                        Platform.runLater(() -> {
                            responseLabel.setText("Error downloading invoice"); // Show error message on the UI
                        });
                    }
                })
                // Handle exceptions that occur during the async request
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        responseLabel.setText("Error: " + throwable.getMessage()); // Show error message on the UI
                    });
                    return null;
                });
    }

    // Define Method to fetch customer details by customer ID, can throw URISyntaxException, IOException, and InterruptedException
    @FXML
    private void getCustomerDetailByCustomerId() throws URISyntaxException, IOException, InterruptedException {

        // Create an HTTP GET request with the customer ID appended to the base URL
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(CUSTOMER_URL + customerId.getText()))
                .GET()
                .build();

        // Send the request and get the response as a string
        HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        // Check if the response is not null and get the response body
        if (response != null) {
            String response1 = response.body();

            // If the response contains the string, update the response label and make the download button visible
            if (response1.contains("Collection presses started")){
                responseLabel.setText(response1);
                responseLabel.setStyle("-fx-text-fill: black;");
                downloadButton.setVisible(true);

                // If the response does not contain the string, update the response label with an error style
            }else{
                responseLabel.setText(response1);
                responseLabel.setStyle("-fx-text-fill: red;");
            }


        }


    }


}
