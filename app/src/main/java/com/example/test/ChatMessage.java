package com.example.test;

import java.io.Serializable;

/**
 * ChatMessage is the model class for a single chat message.
 *
 * @author R.H.Ramawickrama
 */
public class ChatMessage implements Serializable {

    private boolean left;
    private String name;
    private String message;
    private String time;
    private long timeinseconds;
    private String date;

    /**
     * Class constructor.
     *
     * @param left          side of the chat bubble
     * @param message       single conversation message
     * @param time          time in am or pm
     * @param timeinseconds time in seconds
     * @param date          date
     */
    public ChatMessage(boolean left, String message, String time, long timeinseconds, String date) {
        super();
        this.left = left;
        this.message = message;
        this.time = time;
        this.timeinseconds = timeinseconds;
        this.date = date;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTimeinseconds() {
        return timeinseconds;
    }

    public void setTimeinseconds(long timeinseconds) {
        this.timeinseconds = timeinseconds;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}