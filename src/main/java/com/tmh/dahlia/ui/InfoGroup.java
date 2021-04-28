package com.tmh.dahlia.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class InfoGroup extends VBox {
    public InfoGroup(String title) {
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 16));
        VBox.setMargin(titleLabel, new Insets(0, 0, 10, 0));
        getChildren().add(titleLabel);

        setPadding(new Insets(20, 10, 20, 10));
        VBox.setMargin(this, new Insets(10, 10, 10, 10));
        setBorder(new Border(new BorderStroke(Color.web("#d0d0d0"), BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(1))));
    }
}
