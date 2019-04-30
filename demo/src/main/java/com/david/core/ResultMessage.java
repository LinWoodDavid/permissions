package com.david.core;

/**
 * =================================
 * Created by David on 2018/12/5.
 * mail:    17610897521@163.com
 * 描述:      响应提示信息
 */

public enum ResultMessage {

    SUCCESS("SUCCESS"),//成功
    FAIL("FAIL"),//失败
    UNAUTHORIZED("UNAUTHORIZED"),//签名认证失败
    NO_PERMISSIONS("NO_PERMISSIONS"),//无权限(权限认证失败)
    NOT_FOUND("NOT_FOUND"),//接口不存在
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR");//服务器内部错误



    private final String message;

    ResultMessage(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }

}
