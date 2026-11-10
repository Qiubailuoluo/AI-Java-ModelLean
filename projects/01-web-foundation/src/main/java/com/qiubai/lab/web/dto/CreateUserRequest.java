package com.qiubai.lab.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增用户的请求 DTO（数据传输对象），只包含客户端需要提交的字段。
 * 注解声明校验规则；需要调用 Validator，或在后续 Controller 中使用 @Valid 才会触发校验。
 */
// Lombok：生成 getter/setter、toString、equals/hashCode 和所需参数构造器。
// 当前无 final、@NonNull 字段，生成的构造器无参数；不等于生成全字段构造器。
// @Data 不执行参数校验，也不负责数据库读写。
@Data
public class CreateUserRequest {

    // 以下三个注解来自 Jakarta Validation；message 是违反相应规则时的提示。
    @NotBlank(message = "用户名不能为空") // 拒绝 null、空字符串和纯空白。
    @Size(min = 3, max = 32, message = "用户名长度必须为3～32位") // 上下限都包含。
    @Pattern(regexp = "[A-Za-z0-9_]+", message = "用户名只能包含英文字母、数字和下划线")
    private String username;

    @NotBlank(message = "昵称不能为空")
    // String 的 @Size 按 UTF-16 代码单元计数；部分表情会占两个长度单位。
    @Size(min = 1, max = 50, message = "昵称长度必须为1～50个长度单位")
    private String nickname;
}
