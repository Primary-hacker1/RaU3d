package com.rick.recoveryapp.entity.protocol;

public class PoolMessage {

    private String objectJson;

    public String getObjectJson() {
        return objectJson;
    }

    public void setObjectJson(String objectJson) {
        this.objectJson = objectJson;
    }

    public String getObjectName() {
        return ObjectName;
    }

    public void setObjectName(String objectName) {
        ObjectName = objectName;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    private String ObjectName;
    private boolean state;

}
