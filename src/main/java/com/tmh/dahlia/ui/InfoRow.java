package com.tmh.dahlia.ui;

import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class InfoRow extends HBox {
    private final Label valueLabel = new Label();

    public InfoRow(String title, String defaultValue) {
        Label titleLabel = new Label(title);
        valueLabel.setText(defaultValue);
        Pane separator = new Pane();
        HBox.setHgrow(separator, Priority.ALWAYS);
        getChildren().addAll(titleLabel, separator, valueLabel);

        VBox.setMargin(this, new Insets(10, 0, 0, 0));
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}
