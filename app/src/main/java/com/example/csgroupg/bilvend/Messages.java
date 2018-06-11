package com.example.csgroupg.bilvend;

/**
 * Messages Class
 * @author Bilvend
 * @version 11.05.2018
 */
public class Messages {

    //parameters
    private String message, type;
    private long  time;
    private boolean seen;
    private String from;

    //constructors
    public Messages(String from) {
        this.from = from;
    }

    public Messages(String message, String type, long time, boolean seen) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
    }

    public Messages(){

    }

    /**
     * This method returns the address of the message
     * @return the address of the message
     */
    public String getFrom() {

        return from;
    }

    /**
     * This method sets the address of the message
     * @param from the address of the message
     */
    public void setFrom(String from) {

        this.from = from;
    }

    /**
     * This method returns the message
     * @return message
     */
    public String getMessage() {

        return message;
    }

    /**
     * This method sets the message
     * @param message the message
     */
    public void setMessage(String message) {

        this.message = message;
    }

    /**
     * This method returns the type of the message
     * @return the type
     */
    public String getType() {

        return type;
    }

    /**
     * This method sets the type of the message
     * @param type the type
     */
    public void setType(String type) {

        this.type = type;
    }

    /**
     * This method returns the time
     * @return the time
     */
    public long getTime() {

        return time;
    }

    /**
     * This method sets the time
     * @param time the time
     */
    public void setTime(long time) {

        this.time = time;
    }

    /**
     * This method returns whether the message is seen or not
     * @return the truth value
     */
    public boolean isSeen() {

        return seen;
    }

    /**
     * This method sets whether the message is seen or not
     * @param seen the truth value
     */
    public void setSeen(boolean seen) {

        this.seen = seen;
    }


}
