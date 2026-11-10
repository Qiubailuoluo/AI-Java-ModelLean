# UserService 与构造器注入

本笔记记录初版具体 Service 类的学习过程。随后已拆分为 service/UserService.java 接口与 service/impl/UserServiceImpl.java 实现类；具体代码和事务现在位于实现类，详见 [[09-Service接口与实现]]。

## 本轮业务流程

CreateUserRequest → User → Mapper 插入 → 取得回填 ID → Mapper 按 ID 查询 → UserResponse。
Service 不负责 HTTP 状态码或 ApiResponse 外壳。当前没有 Controller，Postman 尚不能调用新增用户接口。

## 三个注解

- @Service：Spring 组件标记，当前组件扫描会发现该类并注册业务对象。
- @RequiredArgsConstructor：Lombok 编译期生成构造器。当前只有一个未初始化的 final 字段 UserMapper，因此相当于生成 UserService(UserMapper userMapper) 并赋值。
- Spring 对只有一个构造器的组件，可以直接使用该构造器注入依赖，不需要再写 @Autowired。Lombok 只生成构造器，依赖注入由 Spring 完成。
- @Transactional：Spring 事务注解。当前通过容器中的代理从外部调用 create 时，在事务中执行；正常完成提交，默认 RuntimeException/Error 导致回滚。不是所有异常都默认回滚；同类内直接自调用也不能简单认为一定经过代理。

初版 UserService 是具体类；后续按用户要求确立本仓库的接口/实现组织约定并完成拆分。框架不强制必须拆分，项目中保持约定一致。

## 方法中的每一步

1. new User()：创建数据库记录对象。
2. setUsername/setNickname：将请求中的允许字段复制到记录对象。
3. insert：返回影响行数，同时回填 user.id。
4. 检查影响行数和 ID，遇异常结果抛出 IllegalStateException。
5. findById：从数据库回读创建时间等实际值；如果没有记录，抛出运行时异常。
6. new UserResponse：只组装对外允许返回的四个字段。

用户名重复时，数据库唯一约束保证不会插入第二条。当前 DuplicateKeyException 继续向外抛出；后续再实现其到业务错误/HTTP 409 的转换，不能声称当前已经有完整错误响应。
字段格式校验需要后续 Controller 通过 @Valid 触发；当前集成验证提供的是已知合法输入。

## 实际验证和保留数据

Maven compile 成功。取得真实 Spring UserService 组件执行创建，事务提交后用 JDBC 回读比对；再次创建同名用户产生预期 DuplicateKeyException，记录数仍为 1。

| 项目 | 值 |
| --- | --- |
| 数据库 | lab_web_foundation |
| 表 | app_user |
| id | 4 |
| username | learn_user_01 |
| nickname | 学习用户一号 |
| created_at | 2026-11-10 12:58:36 |

按用户要求保留该记录，没有回滚或清理成功创建的数据。可用于未来按 ID 查询，以及重复用户名失败案例。自增 ID 不保证后续连续。

```sql
SELECT id, username, nickname, created_at
FROM lab_web_foundation.app_user
WHERE id = 4;
```

验证后关闭应用上下文和连接池，没有启动 HTTP 服务。
后续普通练习默认保留成功数据，专门演示回滚的实验提前说明；业务失败需要的事务回滚仍保留。

## 下一步

理解依赖注入、事务与创建流程后，再补按 ID 查询的业务处理和业务异常，随后逐步接 Controller 与全局异常响应，不一次生成完整模块。

## 参考

- https://docs.spring.io/spring-framework/reference/core/beans/annotation-config/autowired.html
- https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html
