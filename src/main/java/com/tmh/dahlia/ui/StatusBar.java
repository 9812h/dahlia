package com.tmh.dahlia.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class StatusBar extends Row {
    private final Label contentLabel = new Label("Welcome!");

    public StatusBar() {
        super();
        getChildren().add(contentLabel);

        HBox.setHgrow(contentLabel, Priority.ALWAYS);

        setPadding(new Insets(10, 10, 10, 10));
        VBox.setMargin(this, new Insets(10, 10, 5, 10));
//        setBorder(new Border(new BorderStroke(Color.web("#e1e4e8"), BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(1))));
    }

    public void setContent(String content) {
        contentLabel.setText(content);
    }
}
