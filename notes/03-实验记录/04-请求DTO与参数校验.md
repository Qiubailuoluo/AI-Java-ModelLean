# 请求 DTO 与参数校验

源码：projects/01-web-foundation/src/main/java/com/qiubai/lab/web/dto/CreateUserRequest.java。

DTO 是数据传输对象；当前请求只接收 username、nickname，不接收服务端生成的 id、createdAt。

## 注解的来源和作用

- @Getter、@Setter：Lombok，在编译时生成访问方法。
- @NotBlank：Jakarta Validation，拒绝 null、空字符串和纯空白。
- @Size：Jakarta Validation，约束长度，上下限包含；自身允许 null。
- @Pattern：Jakarta Validation，要求整个字符串匹配正则；自身允许 null。
- message：对应规则违反时的提示，不负责直接生成 HTTP 响应。

正则 [A-Za-z0-9_]+ 表示一个或多个英文字母、数字或下划线；3～32 的长度约束单独由 @Size 提供。
多个约束相互配合，不保证按源码顺序执行，同一个值可能产生多项违反结果。

## 生效条件

setter 只赋值；创建对象或调用 setter 不会自动校验。
可以显式调用 Validator.validate(request)。后续 Controller 中使用 @Valid 配合 @RequestBody，让 Spring MVC 触发校验。
校验失败怎样转换为约定的 400 响应和统一 JSON，需要后续异常处理实现；当前尚无 HTTP 接口。
用户名是否已经存在需要数据库/业务层判断，不能靠这三个格式校验注解完成。

## 长度边界

String 的 @Size 按 UTF-16 代码单元计数。常见汉字通常占一个，部分表情占两个；它与 MySQL VARCHAR 的字符计数不是完全同一规则。
本阶段已在接口约定中明确该口径；以后需要按 Unicode 码点或用户可见字符计数时再调整设计。

## 实测结果

Maven compile 成功。使用 Hibernate Validator 实际执行六组输入：

| 场景 | 违反规则数量 |
| --- | --- |
| 合法用户名与中文昵称 | 0 |
| 用户名含空格，昵称全为空格 | 2 |
| 两个字段都为 null | 2 |
| 用户名长度 3、昵称长度 1 | 0 |
| 用户名长度 32、昵称长度 50 | 0 |
| 用户名长度 33、昵称长度 51 | 2 |

没有启动服务或操作数据库。

## 参考

- https://jakarta.ee/specifications/bean-validation/3.0/apidocs/jakarta/validation/constraints/NotBlank.html
- https://jakarta.ee/specifications/bean-validation/3.0/apidocs/jakarta/validation/constraints/Size.html
- https://jakarta.ee/specifications/bean-validation/3.0/apidocs/jakarta/validation/constraints/Pattern.html
