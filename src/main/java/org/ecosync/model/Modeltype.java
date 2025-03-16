package org.ecosync.model;

import org.ecosync.storage.StorageName;

import java.util.Date;

@StorageName("tc_modeltypes")
public class Modeltype extends BaseModel{

    private  String name;
    public String getName(){return name;}
    public void setName(String name){this.name=name;}

    private long manufacturerId;
    public long getManufacturerId() {return manufacturerId;}
    public void setManufacturerId(long manufacturerId) {this.manufacturerId = manufacturerId;}

    private Date createdOn;
    public Date getCreatedOn() {return createdOn;}
    public void setCreatedOn(Date createdOn) {this.createdOn = createdOn;}

    private long createdBy;
    public long getCreatedBy() {return createdBy;}
    public void setCreatedBy(long createdBy) {this.createdBy = createdBy;}

    private long modifiedBy;
    public long getModifiedBy() {return modifiedBy;}
    public void setModifiedBy(long modifiedBy) {this.modifiedBy = modifiedBy;}


}
