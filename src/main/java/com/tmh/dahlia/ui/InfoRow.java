package com.tmh.dahlia.ui;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class InfoRow extends Row {
    private final Label valueLabel = new Label();

    public InfoRow(String title, String defaultValue) {
        Label titleLabel = new Label(title);
        valueLabel.setText(defaultValue);
        Pane separator = new Pane();
        HBox.setHgrow(separator, Priority.ALWAYS);
        getChildren().addAll(titleLabel, separator, valueLabel);
    }

    public void setValue(String value) {
        Platform.runLater(() -> valueLabel.setText(value));
    }
}
