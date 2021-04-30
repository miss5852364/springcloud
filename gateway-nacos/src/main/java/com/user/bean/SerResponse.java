package com.user.bean;

public class SerResponse {

    private String code;

    private String msg;

    private Object data;


    public static SerResponse error(String msg) {
        SerResponse response = new SerResponse();
        response.setCode("301");
        response.setMsg(msg);
        return response;
    }


    public SerResponse() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public SerResponse(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
