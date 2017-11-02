package com.example.phone.activity;

/**
 * @author suhu
 * @data 2017/10/31.
 * @description
 */

public class ItemMessage {
    private String message;
    private int typ;

    public ItemMessage(String message, int typ) {
        this.message = message;
        this.typ = typ;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTyp() {
        return typ;
    }

    public void setTyp(int typ) {
        this.typ = typ;
    }
}
