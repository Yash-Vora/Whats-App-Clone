package com.example.whatsapp.Models;

public class MessagesModel {

    String uid, message, messageId, time;
    Long timestamp;


    // Constructor
    public MessagesModel(String uid, String message, Long timestamp) {
        this.uid = uid;
        this.message = message;
        this.timestamp = timestamp;
    }

    // ChatDetailActivity Constructor
    public MessagesModel(String uid, String message) {
        this.uid = uid;
        this.message = message;
    }

    // Empty Constructor
    public MessagesModel(){

    }


    // Getters and Setters methods
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
