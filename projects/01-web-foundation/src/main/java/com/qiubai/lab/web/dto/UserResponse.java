package com.qiubai.lab.web.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回给客户端的用户信息，只声明本接口需要输出的字段。
 * 它是 ApiResponse<UserResponse> 中的 data，不包含 code、message。
 */
@Getter // Lombok：生成 getter，供调用方和 JSON 序列化读取属性。
@AllArgsConstructor // 按 id、username、nickname、createdAt 的顺序生成构造器。
public class UserResponse {

    private final Long id;

    private final String username;

    private final String nickname;

    // 输出数据库实际保存的创建时间；本类本身不执行查询或生成时间。
    private final LocalDateTime createdAt;
}
