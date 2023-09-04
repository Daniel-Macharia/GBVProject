package com.example.frats;

public class chatMessage {

    private String message;

    private int gravity;

    private String time;

    public chatMessage(String message, String time, int gravity )
    {
        this.message = message;
        this.time = time;
        this.gravity = gravity;
    }

    public String getMessage()
    {
        return message;
    }

    public String getTime()
    {
        return time;
    }

    public int getGravity(){ return gravity; }
}
