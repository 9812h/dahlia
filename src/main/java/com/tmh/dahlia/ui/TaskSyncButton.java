package com.tmh.dahlia.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class TaskSyncButton extends Button {
    public TaskSyncButton(String title, Color fg, Color bg) {
        setPadding(new Insets(5, 15, 5, 15));
        setFont(Font.font(13));

        setText(title);
        setBackground(new Background(new BackgroundFill(bg, new CornerRadii(4), Insets.EMPTY)));
        setTextFill(fg);
    }

    public void disable() {
        setDisable(true);
    }

    public void enable() {
        setDisable(false);
    }
}
