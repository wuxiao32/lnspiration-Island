package com.inspiration.island.exception;

import com.inspiration.island.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 通用异常捕获
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e){
        e.printStackTrace();
        return Result.error(500,"服务器内部异常："+e.getMessage());
    }
}