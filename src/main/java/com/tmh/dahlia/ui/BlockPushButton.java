package com.tmh.dahlia.ui;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class BlockPushButton extends Button {
    public static class State {
        private String name;
        private Color fg, bg;
        public State(String name, Color fg, Color bg) {
            this.name = name; this.fg = fg; this.bg = bg;
        }

        public String getName() {
            return name;
        }

        public Color getFg() {
            return fg;
        }

        public Color getBg() {
            return bg;
        }
    }

    private State state1, state2;
    private State currentState;

    public BlockPushButton(State s1, State s2) {
        super(s1.getName());
        state1 = s1; state2 = s2;

        setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this, Priority.ALWAYS);
        setPadding(new Insets(5, 15, 5, 15));
        setFont(Font.font(13));

        apply(state1);

        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (currentState == state1) apply(state2); else apply(state1);
            }
        });
    }

    public State getCurrentState() { return this.currentState; }

    private void apply(State state) {
        this.currentState = state;
        setText(state.name);
        setBackground(new Background(new BackgroundFill(state.getBg(), new CornerRadii(4), Insets.EMPTY)));
        setTextFill(state.getFg());
    }

}
