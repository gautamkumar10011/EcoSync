package org.ecosync.model;

import org.ecosync.storage.StorageName;

@StorageName("tc_states")
public class State extends  BaseModel{

    private String name ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
