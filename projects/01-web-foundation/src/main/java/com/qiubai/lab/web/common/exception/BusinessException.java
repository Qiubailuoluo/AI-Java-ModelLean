package com.qiubai.lab.web.common.exception;

import lombok.Getter;

/**
 * 可识别的业务错误：携带应用错误代码和说明，不决定 HTTP 状态码。
 * 继承 RuntimeException，使当前 Spring 事务默认回滚规则能够处理它。
 */
@Getter // Lombok：为 code 生成 getCode()；getMessage() 继承自异常父类。
public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(String code, String message) {
        super(message); // 将说明交给父类，之后通过 getMessage() 读取。
        this.code = code;
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause); // 保留底层异常原因，便于后续服务端定位问题。
        this.code = code;
    }
}
