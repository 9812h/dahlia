package com.tmh.dahlia.ui;

import com.tmh.dahlia.SessionTimer;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class BlockPushButton extends Button {
    private enum State { ON, OFF }
    public enum EventType { ON, OFF }

    public interface EventListener { void work(); }

    private final Map<BlockPushButton.EventType, HashSet<BlockPushButton.EventListener>> eventListeners = new HashMap<>();

    public static class Profile {
        private String name;
        private Color fg, bg;
        public Profile(String name, Color fg, Color bg) {
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

    private Profile stateOnProfile, stateOffProfile;
    private State currentState;

    public BlockPushButton(Profile p1, Profile p2) {
        super(p1.getName());
        stateOnProfile = p1; stateOffProfile = p2;
        for (BlockPushButton.EventType event : BlockPushButton.EventType.class.getEnumConstants()) eventListeners.put(event, new HashSet<>());

        setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this, Priority.ALWAYS);
        setPadding(new Insets(5, 15, 5, 15));
        setFont(Font.font(13));

        currentState = State.OFF;
        apply_appearance_for(currentState);

        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (currentState == State.ON) {
                    currentState = State.OFF;
                    triggerListenerCallbacks(EventType.OFF);
                } else {
                    currentState = State.ON;
                    triggerListenerCallbacks(EventType.ON);
                }
                apply_appearance_for(currentState);
            }
        });
    }

    public State getCurrentState() { return this.currentState; }

    public void addEventListener(BlockPushButton.EventType eventType, BlockPushButton.EventListener listener) {
        eventListeners.get(eventType).add(listener);
    }

    public void removeEventListener(BlockPushButton.EventType eventType, BlockPushButton.EventListener listener) {
        eventListeners.get(eventType).remove(listener);
    }

    private void triggerListenerCallbacks(BlockPushButton.EventType eventType) {
        for (BlockPushButton.EventListener listener : eventListeners.get(eventType)) {
            Thread t = new Thread(() -> listener.work(), "");
            t.start();
        }
    }

    private void apply_appearance_for(State state) {
        Profile profileToApply = (state == State.ON) ? stateOnProfile : stateOffProfile;
        setText(profileToApply.name);
        setBackground(new Background(new BackgroundFill(profileToApply.getBg(), new CornerRadii(4), Insets.EMPTY)));
        setTextFill(profileToApply.getFg());
    }

    public void disable() {
        Platform.runLater(() -> setDisable(true));
    }

    public void enable() {
        Platform.runLater(() -> setDisable(false));
    }

}
