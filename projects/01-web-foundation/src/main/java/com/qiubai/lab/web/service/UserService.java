package com.qiubai.lab.web.service;

import com.qiubai.lab.web.dto.CreateUserRequest;
import com.qiubai.lab.web.dto.UserResponse;

/**
 * 用户业务接口：声明提供什么能力，不放 SQL 或具体实现步骤。
 * 调用方依赖此接口；具体业务由 service.impl.UserServiceImpl 实现。
 */
public interface UserService {

    /**
     * 创建用户，返回数据库实际保存的对外信息。
     * 用户名冲突等异常继续向外传播，HTTP 错误响应后续单独处理。
     */
    UserResponse create(CreateUserRequest request);

    /**
     * 按正整数 ID 查询用户；ID 不合法或用户不存在时抛出业务异常。
     */
    UserResponse findById(Long id);
}
