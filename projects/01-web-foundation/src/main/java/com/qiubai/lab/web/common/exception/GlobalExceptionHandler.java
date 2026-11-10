package com.qiubai.lab.web.common.exception;

import com.qiubai.lab.web.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 集中处理 Spring MVC 请求链路中的异常，生成统一 JSON 响应。
 * 不会自动捕获后台线程、定时任务或应用启动阶段的所有异常。
 */
@Slf4j // Lombok：生成日志对象 log，详细内部错误记录在服务端。
@RestControllerAdvice // Spring MVC：全局控制器增强，返回对象按响应体进行序列化。
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class) // 指定此方法处理的异常类型。
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException exception) {
        // 根据明确约定的业务代码选择状态，不把所有业务异常都默认映射为 409。
        if ("USERNAME_ALREADY_EXISTS".equals(exception.getCode())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(exception.getCode(), exception.getMessage(), null));
        }
        if ("USER_NOT_FOUND".equals(exception.getCode())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(exception.getCode(), exception.getMessage(), null));
        }
        if ("INVALID_ARGUMENT".equals(exception.getCode())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(exception.getCode(), exception.getMessage(), null));
        }
        // 没有约定映射的业务代码按内部错误处理，避免直接对外暴露内部信息。
        return handleUnexpected(exception);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException exception) {
        // 本阶段返回一条字段错误，不保证字段或约束的报错顺序。
        FieldError error = exception.getBindingResult().getFieldError();
        String message = error == null ? "请求参数不合法"
                : error.getField() + ": " + error.getDefaultMessage();
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>("INVALID_ARGUMENT", message, null));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch() {
        // 例如路径id=abc或超出Long范围：尚未进入Controller方法体就转换失败。
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>("INVALID_ARGUMENT", "请求参数类型不正确或超出取值范围", null));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableBody() {
        // 覆盖 JSON 格式错误、字段类型转换失败和缺少必需请求体等情况。
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>("INVALID_ARGUMENT", "请求体缺失或JSON格式不正确", null));
    }

    @ExceptionHandler(Exception.class) // 兜底；更具体的匹配优先，不是按方法书写顺序匹配。
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception exception) {
        if (exception instanceof ErrorResponse errorResponse) {
            // 保留 Spring MVC 已确定的状态和响应头，例如 404、405 及 Allow 头。
            // 避免把不存在的路径、错误的请求方法一律改成 500。
            return ResponseEntity.status(errorResponse.getStatusCode())
                    .headers(errorResponse.getHeaders())
                    .body(new ApiResponse<>("HTTP_ERROR", "请求无法处理", null));
        }
        log.error("请求处理发生未预期异常", exception);
        // 只返回通用说明，不把 SQL、异常栈或底层 cause 序列化给客户端。
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("INTERNAL_ERROR", "服务暂时不可用，请稍后重试", null));
    }
}
