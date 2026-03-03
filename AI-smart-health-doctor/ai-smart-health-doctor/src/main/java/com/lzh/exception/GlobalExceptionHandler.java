package com.lzh.exception;

import com.lzh.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Result handleException(Exception e) {
        log.error("发生系统异常:", e);
        return Result.error("系统繁忙或发生内部错误，请稍后再试或联系管理员");
    }
}