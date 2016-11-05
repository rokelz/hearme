package com.example.test;

/**
 * Participant is the model class representing a participant of a conversation.
 *
 * @author R.H.Ramawickrama
 */
public class Participant {

    private String name;
    private String color;
    private boolean isRecognized;

    /**
     * Class constructor.
     *
     * @param name         participant name
     * @param color        chat bubble color
     * @param isRecognized is the participant recognized
     */
    public Participant(String name, String color, boolean isRecognized) {
        this.name = name;
        this.color = color;
        this.isRecognized = isRecognized;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isRecognized() {
        return isRecognized;
    }

    public void setRecognized(boolean isRecognized) {
        this.isRecognized = isRecognized;
    }

}
