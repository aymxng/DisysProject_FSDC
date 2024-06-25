package at.technikum.javafx;

//Import necessary classes from JavaFX and IO libraries
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CustomerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CustomerApplication.class.getResource("Customer.fxml"));
        //Load the fxml file and create scene with the loaded content
        Scene scene = new Scene(fxmlLoader.load());
        //Set the title of the primary stage (window)
        stage.setTitle("************Customer Detail ! ************");
        // set the scene and therefore the visuals
        stage.setScene(scene);
        // Make the window visible
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}