package com.qiubai.lab.web.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 多个接口共用的响应包装：code、message、data。
 * T 是业务数据的类型；本类不负责决定 HTTP 状态码。
 */
@Getter // Lombok：生成读取方法，后续 JSON 序列化可以读取这些属性。
@AllArgsConstructor // Lombok：按字段声明顺序生成三个参数的构造器。
public class ApiResponse<T> {

    // 应用结果代码，如 SUCCESS；不是 HTTP 状态码。
    private final String code;

    // 给人阅读的结果说明。
    private final String message;

    // 具体业务数据；失败时可以为 null。
    // final 禁止重新赋值该引用，不保证引用指向的业务对象内部不可变。
    private final T data;
}
