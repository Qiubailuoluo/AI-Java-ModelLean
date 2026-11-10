package com.qiubai.lab.web.controller;

import com.qiubai.lab.web.common.ApiResponse;
import com.qiubai.lab.web.dto.CreateUserRequest;
import com.qiubai.lab.web.dto.UserResponse;
import com.qiubai.lab.web.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户 HTTP 入口：接收请求、触发校验、调用业务接口并组装成功响应。
 * 不写 SQL；异常向外传播，由 GlobalExceptionHandler 统一处理。
 */
@RestController // Spring MVC：注册控制器，返回对象通过消息转换器写入响应体。
@RequestMapping("/api/users") // 此控制器的公共路径。
@RequiredArgsConstructor // Lombok：生成注入 UserService 接口的构造器。
public class UserController {

    private final UserService userService;
    //构造器-示例
    // public UserController(UserService userService) {
    //     this.userService = userService;
    // }

    @PostMapping // POST /api/users。
    public ResponseEntity<ApiResponse<UserResponse>> create(
            @Valid @RequestBody CreateUserRequest request) {
        // @RequestBody 将 JSON 转为 DTO，@Valid 触发 DTO 上的校验。
        // 反序列化或校验失败时不会执行下面的业务调用。
        UserResponse data = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED) // HTTP 201，而不是默认的 200。
                .body(new ApiResponse<>("SUCCESS", "用户创建成功", data));
    }

    @GetMapping("/{id}") // 与类级路径组合：GET /api/users/{id}。
    public ResponseEntity<ApiResponse<UserResponse>> findById(
            @PathVariable("id") Long id) {
        // 从URL路径取得id并转换为Long；正整数检查与未找到处理由Service完成。
        UserResponse data = userService.findById(id);
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "查询成功", data));
    }
}
