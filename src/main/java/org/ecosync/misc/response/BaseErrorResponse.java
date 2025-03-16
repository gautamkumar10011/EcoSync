package org.ecosync.misc.response;

import java.util.List;

public class BaseErrorResponse extends BaseResponse{

    private List<String> errors;

    public BaseErrorResponse(String status, String message, List<String> errors){
        super(status, message);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
