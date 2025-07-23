package com.shemuel.timeline.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.util.SaResult;
import com.shemuel.timeline.common.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * @Author: 公众号: 加瓦点灯
 * @Date: 2025-03-12-11:15
 * @Description:
 */
@RestControllerAdvice
@Configuration
@Slf4j
public class RestExceptionController {

    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public SaResult handleError(NotLoginException e) {
        return SaResult.code(401).setMsg("未登录或登录状态已过期");
    }

    @ExceptionHandler(ServiceException.class)
    public RestResult handleError(ServiceException e) {
        return RestResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public RestResult handleError(BusinessException e) {
        return RestResult.error(e.getCode(), e.getMessage());
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.OK)
    public RestResult handleError(MissingServletRequestParameterException e) {
        log.warn("缺少请求参数", e.getMessage());
        String message = String.format("缺少必要的请求参数: %s", e.getParameterName());
        return RestResult.fail(500, message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.OK)
    public RestResult handleError(MethodArgumentTypeMismatchException e) {
        log.warn("请求参数格式错误", e.getMessage());
        String message = String.format("请求参数格式错误: %s", e.getName());
        return RestResult.fail(500, message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public RestResult handleError(MethodArgumentNotValidException e) {
        log.warn("参数验证失败" + e.getMessage());
        return RestResult.fail(500, e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public RestResult handleError(BindException e) {
        log.warn("参数绑定失败", e.getMessage());
        return RestResult.fail(500, e.getMessage());
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.OK)
    public RestResult handleError(HttpMessageNotReadableException e) {
        log.error("消息不能读取:{}", e.getMessage());
        return RestResult.fail(500, "消息不能读取");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public RestResult handleError(HttpRequestMethodNotSupportedException e) {
        log.error("不支持当前请求方法:{}", e.getMessage());
        return RestResult.fail(405,
                "不支持当前请求方法");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public RestResult handleError(HttpMediaTypeNotSupportedException e) {
        log.error("不支持当前媒体类型:{}", e.getMessage());
        return RestResult.fail(500,
                "不支持当前媒体类型");
    }
}
