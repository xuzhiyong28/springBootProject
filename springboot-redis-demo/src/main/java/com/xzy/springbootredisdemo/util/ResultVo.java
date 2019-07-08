package com.xzy.springbootredisdemo.util;

import com.xzy.springbootredisdemo.emus.BaseEnum;
import com.xzy.springbootredisdemo.emus.SystemResultEnum;

import java.io.Serializable;

public class ResultVo<T> implements Serializable{
    private String code;
    private String msg;
    private T data;

    public ResultVo(){}

    public ResultVo(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultVo(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ResultVo getSuccessResultVo(){
        return ResultVo.getCommonResultVo(SystemResultEnum.SUCCESS);
    }

    public static ResultVo getErrorResultVo(String msg){
        ResultVo resultVo = new ResultVo();
        resultVo.setCode("-1");
        resultVo.setMsg(msg);
        return resultVo;
    }

    public static ResultVo getCommonResultVo(BaseEnum baseEnum){
        ResultVo resultVo = new ResultVo();
        resultVo.setCode(baseEnum.getCode());
        resultVo.setMsg(baseEnum.getMsg());
        return resultVo;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
