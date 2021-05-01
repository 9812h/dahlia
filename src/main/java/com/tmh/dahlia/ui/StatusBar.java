package com.tmh.dahlia.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class StatusBar extends Row {
    private final Label contentLabel = new Label("Welcome");
    private final Label statusLabel = new Label("DEMO");

    public StatusBar() {
        super();

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(contentLabel, spacer, statusLabel);

        setPadding(new Insets(10, 10, 10, 10));
        VBox.setMargin(this, new Insets(10, 10, 5, 10));
    }

    public void setContent(String content) {
        Platform.runLater(() -> { contentLabel.setText(content); });
    }
    public void setStatusLabel(String status)  {
        Platform.runLater(() -> { statusLabel.setText(status); });
    }
}
