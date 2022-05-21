package com.scenes.Modal;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Modal {
    public ImageView icon;

    public static void show(String title, String caption, String text, Icon icon) {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(Modal.class.getResource("Modal.fxml"));
        try {
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);
            ((Label) scene.lookup("#caption")).setText(caption);
            ((TextArea) scene.lookup("#text")).setText(text);
            ((Button) scene.lookup("#closeBtn")).setOnAction((e) -> stage.close());

            Image img = null;
            switch (icon) {
                case error -> {
                    img = new Image(Objects.requireNonNull(Modal.class.getResourceAsStream("/com/assets/images/modal-icons/error.png")));
                }
                case success -> {
                    img = new Image(Objects.requireNonNull(Modal.class.getResourceAsStream("/com/assets/images/modal-icons/success.png")));
                }
                case information -> {
                    img = new Image(Objects.requireNonNull(Modal.class.getResourceAsStream("/com/assets/images/modal-icons/information.png")));
                }
            }

            ((ImageView) scene.lookup("#icon")).setImage(img);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    public enum Icon {
        error,
        success,
        information
    }
}
