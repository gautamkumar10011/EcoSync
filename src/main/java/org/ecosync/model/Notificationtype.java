package org.ecosync.model;


import org.ecosync.storage.StorageName;

@StorageName("tc_notificationtypes")
public class Notificationtype extends BaseModel {

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}