package com.cashrich.coinrich.vo;

public class ResponseVo {
    private int val;
    private String response;

    public ResponseVo() {
        super();
    }

    public ResponseVo(int val, String response) {
        this.val = val;
        this.response = response;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
