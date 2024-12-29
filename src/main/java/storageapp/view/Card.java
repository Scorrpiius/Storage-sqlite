package storageapp.view;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class Card extends VBox {
    public Card(String title, String description, String img) {
        Label titleLabel = new Label(title);
        Label descriptionLabel = new Label(description);
        //ImageView  image = new ImageView(img);
        this.getChildren().addAll(titleLabel, descriptionLabel);
        this.setStyle("-fx-border-color: black; -fx-padding: 10;");
    }
}
