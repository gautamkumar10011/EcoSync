package org.ecosync.misc.response;

import org.ecosync.model.BaseModel;

import java.util.List;

public class BaseModelListResponse extends BaseResponse{

    private List<BaseModel> data;
    private int pageNo;
    private int pageSize;
    private int numRecords;

    public BaseModelListResponse(String status, String message, List<BaseModel> entity){
        super(status, message);
        this.data = entity;
    }

    public BaseModelListResponse(String status, String message, List<BaseModel> entity,
                                 int pageNo, int pageSize, int numRecords){
        super(status, message);
        this.data = entity;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.numRecords = numRecords;
    }
    public List<BaseModel> getData() {
        return data;
    }

    public void setData(List<BaseModel> data) {
        this.data = data;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getNumRecords() {
        return numRecords;
    }

    public void setNumRecords(int numRecords) {
        this.numRecords = numRecords;
    }
}
