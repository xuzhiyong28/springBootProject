package com.xzy.springbootredisdemo.emus;

public enum SystemResultEnum implements BaseEnum {

    SUCCESS("1", "成功"),
    FAIL("0", "系统错误"),
    NO_AUTH_USER("2", "未注册用户"),
    NO_AD_USER("3", "待审核用户");

    SystemResultEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private String code;

    private String msg;

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
}
