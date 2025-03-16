package org.ecosync.misc.response;

import org.ecosync.model.BaseModel;

public class BaseModelResponse extends BaseResponse{

    private BaseModel data;

    public BaseModelResponse(String status, String message, BaseModel entity){
        super(status, message);
        this.data = entity;
    }

    public BaseModel getData() {
        return data;
    }

    public void setData(BaseModel data) {
        this.data = data;
    }
}
