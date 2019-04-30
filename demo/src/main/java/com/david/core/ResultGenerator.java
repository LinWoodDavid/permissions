package com.david.core;

/**
 * 响应结果生成工具
 */
public class ResultGenerator {

    public static Result genSuccessResult() {
        return new Result()
                .setCode(ResultCode.SUCCESS)
                .setMessage(ResultMessage.SUCCESS);
    }

    public static <T> Result<T> genSuccessResult(T data) {
        return new Result()
                .setCode(ResultCode.SUCCESS)
                .setMessage(ResultMessage.SUCCESS)
                .setData(data);
    }

    public static Result genFailResult(ResultCode resultCode, ResultMessage resultMessage) {
        return new Result()
                .setCode(resultCode)
                .setMessage(resultMessage);
    }

}
