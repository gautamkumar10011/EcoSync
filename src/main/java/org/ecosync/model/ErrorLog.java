
package org.ecosync.model;

import org.ecosync.storage.StorageName;

import java.util.Date;

@StorageName("tc_errorlogs")
public class ErrorLog extends BaseModel {

    private String component;
    private String errMessage;
    private String solutionDesc;
    private long createdBy;
    private long deviceId;
    private Date createdOn;

    public ErrorLog(){
        this.createdOn = new Date();
    }

    public ErrorLog(String component, String errMessage, long createdBy, long deviceId) {
        this.component = component;
        this.errMessage = errMessage;
        this.createdBy = createdBy;
        this.deviceId = deviceId;
        this.createdOn = new Date();
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getSolutionDesc() {
        return solutionDesc;
    }

    public void setSolutionDesc(String solutionDesc) {
        this.solutionDesc = solutionDesc;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}

