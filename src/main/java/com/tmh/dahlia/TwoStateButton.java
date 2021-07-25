package com.tmh.dahlia;

import javafx.scene.control.Button;

import java.util.ArrayList;

public class TwoStateButton extends Button {
    public enum State {
        STATE_1, STATE_2
    }

    private State state;

    private final ArrayList<EventListener> onChangeToState1Listeners = new ArrayList<>();
    private final ArrayList<EventListener> onChangeToState2Listeners = new ArrayList<>();


    TwoStateButton(String text, State initState) {
        super(text);
        state = initState;
        this.setOnMouseClicked((Event) -> {
            state = state == State.STATE_1 ? State.STATE_2 : State.STATE_1;

            Thread executingThread = new Thread(() -> {
                this.setDisable(true);

                ArrayList<EventListener> listeners = state == State.STATE_1 ? onChangeToState1Listeners : onChangeToState2Listeners;

                ArrayList<Thread> threads = new ArrayList<>();

                for (EventListener eventListener : listeners) {
                    Thread t = new Thread(eventListener::action);
                    threads.add(t);
                    t.start();
                }
                for (Thread t : threads) try { t.join(); } catch (InterruptedException e) { e.printStackTrace(); }

                this.setDisable(false);
            });

            executingThread.start();

        });
    }

    public interface EventListener {
        void action();
    }

    public void setOnChangeToState1(EventListener eventListener) {
        onChangeToState1Listeners.add(eventListener);
    }

    public void unsetOnChangeToState1(EventListener eventListener) {
        onChangeToState1Listeners.remove(eventListener);
    }

    public void setOnChangeToState2(EventListener eventListener) {
        onChangeToState2Listeners.add(eventListener);
    }

    public void unsetOnChangeToState2(EventListener eventListener) {
        onChangeToState2Listeners.remove(eventListener);
    }

}
