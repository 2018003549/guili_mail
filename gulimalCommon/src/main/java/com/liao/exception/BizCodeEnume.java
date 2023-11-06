package com.liao.exception;

public enum BizCodeEnume {
    UNKONW_EXCEPTINO(10000,"系统未知异常"),
    VAILD_EXCEPTION(10001,"参数格式校验失败");
    private  int code;
    private String msg;
    BizCodeEnume(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}
