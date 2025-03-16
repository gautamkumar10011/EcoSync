package org.ecosync.misc.response;

import org.ecosync.model.BaseModel;

public class BaseTokenResponse extends BaseResponse{

    private String token;

    public BaseTokenResponse(String status, String message, String token){
        super(status, message);
        this.token = token;
    }

    public String getToken() {


        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
