package com.david.core;

/**
 * 响应码枚举，参考HTTP状态码的语义
 */
public enum ResultCode {
    SUCCESS(20000),//成功
    FAIL(40000),//失败
    UNAUTHORIZED(40001),//签名认证失败
    NO_PERMISSIONS(40003),//无权限(权限认证失败)
    NOT_FOUND(40004),//接口不存在
    INTERNAL_SERVER_ERROR(50000);//服务器内部错误

    private final int code;

    ResultCode(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
