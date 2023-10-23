package com.rick.recoveryapp.entity;

public class LiveMessage {

    private String Message;
    private Boolean isConnt;
    private String state;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Boolean getIsConnt() {
        return isConnt;
    }

    public void setIsConnt(Boolean connt) {
        isConnt = connt;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }



}
