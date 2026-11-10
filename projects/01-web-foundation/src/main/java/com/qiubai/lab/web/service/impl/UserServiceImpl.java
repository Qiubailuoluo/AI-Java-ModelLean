package com.qiubai.lab.web.service.impl;

import com.qiubai.lab.web.common.exception.BusinessException;
import com.qiubai.lab.web.dto.CreateUserRequest;
import com.qiubai.lab.web.dto.UserResponse;
import com.qiubai.lab.web.entity.User;
import com.qiubai.lab.web.mapper.UserMapper;
import com.qiubai.lab.web.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 组织新增和查询用户的业务流程，不负责 HTTP 状态码或统一响应包装。
 * 新增请求字段由 Controller 触发校验；业务异常由全局异常处理器转换为响应。
 */
@Service // Spring：将此类识别为业务组件，创建对象并交给容器管理。
@RequiredArgsConstructor // Lombok：为未初始化的 final 字段生成构造器。
public class UserServiceImpl implements UserService {

    // Spring 通过生成的唯一构造器，注入之前注册的 UserMapper 代理。
    private final UserMapper userMapper;

    // Java：声明此方法实现 UserService 的约定，签名不匹配时编译器会报错。
    @Override
    // Spring：由外部通过容器代理调用时，正常完成提交事务；默认遇运行时异常或 Error 回滚。
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        // 请求 DTO 转为数据库记录对象；不接收客户端指定的 ID 和创建时间。
        User user = new User();
        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname());

        int affectedRows;
        try {
            // 插入成功后，MyBatis 回填 user.id。
            affectedRows = userMapper.insert(user);
        } catch (DuplicateKeyException exception) {
            // 当前插入不传主键，用户输入涉及的唯一约束只有 username。
            // 只转换重复键错误；连接失败等其他数据库异常继续向外传播。
            throw new BusinessException("USERNAME_ALREADY_EXISTS", "用户名已存在", exception);
        }
        if (affectedRows != 1 || user.getId() == null) {
            throw new IllegalStateException("用户插入结果异常");
        }

        // 使用新 ID 查询实际保存的记录，取得数据库生成的创建时间。
        User savedUser = userMapper.findById(user.getId());
        if (savedUser == null) {
            throw new IllegalStateException("用户插入后未查询到记录");
        }

        // entity 转为响应 DTO，只返回当前接口明确允许输出的字段。
        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getNickname(),
                savedUser.getCreatedAt()
        );
    }

    @Override
    public UserResponse findById(Long id) {
        // || 从左向右短路：id 为 null 时不会继续执行 id <= 0，避免空指针。
        if (id == null || id <= 0) {
            throw new BusinessException("INVALID_ARGUMENT", "用户ID必须为正整数");
        }

        User user = userMapper.findById(id);
        // Mapper 的 null 只表示没有匹配记录；Service 将它解释为明确的业务错误。
        if (user == null) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getCreatedAt()
        );
    }
}
