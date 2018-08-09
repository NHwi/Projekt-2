package com.academy.projekt2;

public class Message {
    private int id;
    private int roomID;
    private int userID;
    private String message;
    Message(int id, int roomID, int userID, String message){
        this.id = id;
        this.roomID = roomID;
        this.userID = userID;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
