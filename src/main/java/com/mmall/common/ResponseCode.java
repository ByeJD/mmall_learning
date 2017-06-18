package com.mmall.common;

/**
 * Created by ADMIN on 2017/6/18.
 */
public enum ResponseCode {

    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    private  final  int code;
    private  final  String desc;

    ResponseCode(int code,String desc){
        this.code = code;
        this.desc = desc;
    }
}