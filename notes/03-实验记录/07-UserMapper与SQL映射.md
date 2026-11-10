# UserMapper 与 SQL 映射

源码：projects/01-web-foundation/src/main/java/com/qiubai/lab/web/mapper/UserMapper.java。

## 本轮范围

只新增 UserMapper 两个方法：insert(User) 和 findById(Long)。不创建 Service、Controller 或 HTTP 业务接口。
当前 SQL 简短，使用注解承载；复杂动态 SQL 后续再学习 XML。注解与 XML 都是 MyBatis 支持的方式，不以是否使用 XML 判断工程质量。

## interface 与代理

UserMapper 是接口，声明可调用的方法。普通抽象接口方法没有方法体，末尾用分号结束。
@Mapper 来自 MyBatis；当前 starter 扫描该标记，为接口建立代理并注册到 Spring。调用代理的方法时，MyBatis 执行对应 SQL。
当前不用手写 UserMapperImpl，也不用 new UserMapper()。这与 Lombok 在编译时生成 getter/setter 的机制不同。

## insert

@Insert 声明 INSERT SQL。#{username}、#{nickname} 读取单个 User 参数对象的对应属性并作为参数绑定，不直接拼接 SQL 文本。不要给占位符再加 SQL 单引号。
最终类似 INSERT INTO app_user (username, nickname) VALUES (?, ?)，再绑定实际值。
省略 id、created_at，使数据库负责自增主键和默认创建时间。
@Options(useGeneratedKeys=true, keyProperty="id") 获取数据库生成的主键，并设置传入对象的 id 属性。keyProperty 指 Java 属性名，不是返回值名称。
int insert(User user) 返回影响行数。插入一条通常返回 1，不是新主键。新 ID 从 user.getId() 读取。
该选项不回填 createdAt；创建时间需使用新主键查询数据库。

## findById

@Select 声明查询 SQL，明确列出四个需要的字段。
@Param("id") 给参数命名，对应 SQL 中的 #{id}。
返回类型 User 说明要将单条查询结果映射为该对象。
id、username、nickname 对应同名属性；created_at 由当前驼峰映射配置对应 createdAt。
没有匹配行时当前方法返回 null，不会自动抛出 HTTP 404。怎样转换为业务异常和 HTTP 响应，后续实现。

## 实际验证

Maven compile 成功。在实际 Spring 上下文中取得 UserMapper 代理，在事务中插入中文昵称用户并查询：

```text
INSERT_AFFECTED_ROWS=1
GENERATED_ID=3
CREATED_AT_READ_FROM_DATABASE=true
MISSING_ID_RETURNS_NULL=true
VERIFICATION_ROWS_AFTER_ROLLBACK=0
MAPPER_VERIFICATION_PASSED
```

ID=3 是本次观测值，不是未来固定结果。验证事务已回滚，不保留测试记录；自增编号可能被消耗。
验证后关闭应用上下文和连接池，没有监听 HTTP 端口。

## 下一步

理解 Mapper 后，逐步创建用户业务处理，串起请求 DTO → User → 插入 → 按新 ID 查询 → UserResponse。重复用户名的处理、事务与依赖注入需要单独解释，不一次生成完整模块。

## 参考

- https://mybatis.org/mybatis-3/java-api.html
- https://mybatis.org/mybatis-3/sqlmap-xml.html
