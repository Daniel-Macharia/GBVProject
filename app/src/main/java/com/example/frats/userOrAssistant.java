package com.example.frats;

public class userOrAssistant {

    private String username;
    private String phone;

    public userOrAssistant( String username, String phone)
    {
        this.username = username;
        this.phone = phone;
    }

    public String getUserName(){ return username; }
    public String getPhone(){ return phone; }

    public void setUserName( String username )
    {
        this.username = username;
    }

    public void setPhone( String phone )
    {
        this.phone = phone;
    }
}
