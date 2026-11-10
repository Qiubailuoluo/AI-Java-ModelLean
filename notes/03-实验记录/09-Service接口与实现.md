# Service 接口与实现

## 本项目组织约定

```text
service/UserService.java
service/impl/UserServiceImpl.java
```

UserService 声明业务能力，UserServiceImpl 编写执行步骤。
这种接口/实现组织方式是本仓库采用的约定，不是 Java 或 Spring 强制要求，也不代表其他项目中的具体 Service 类一定不规范。

## 逐项理解

- interface UserService：接口，只声明 create 的输入和输出。
- class UserServiceImpl implements UserService：实现类承诺满足接口约定。implements 用于实现接口，不是 extends 类继承。
- @Override：Java 自带注解，提示编译器检查当前方法确实重写或实现了对应方法。它不创建 Spring 组件，不执行事务，也不是 Spring 注解。
- @Service：放在具体实现类上，使当前组件扫描可以注册业务对象。
- @RequiredArgsConstructor：放在实现类上，生成注入 Mapper 的构造器。
- @Transactional：本项目放在实现方法上，保留原有事务逻辑；无需复制到接口方法。

调用方依赖 UserService 类型；当前只有一个对应实现，Spring 可以找到 UserServiceImpl 的受管对象/代理。以后有多个实现时需明确选择，不是看到相同方法名就随意匹配。

接口没有自动消除具体实现中的数据库依赖；它隔离的是调用方与具体实现类型。测试替身、替换实现和职责表达是可获得的好处，但接口数量本身不代表设计质量。

## 本轮范围与验证

只重构接口与实现，原有插入、查询、响应组装不变，没有增加 HTTP 接口。
Maven compile 通过；原集成验证代码仍依赖 UserService 类型，无需改成 UserServiceImpl，创建与用户名冲突验证均通过。

新增保留记录：

| id | username | nickname | created_at |
| --- | --- | --- | --- |
| 6 | learn_service_impl_01 | 学习用户一号 | 2026-11-10 13:09:08 |

之前 ID=4 的 learn_user_01 没有被本轮修改或删除。ID 不连续是正常现象，先前重复插入失败也可能消耗编号。

## 后续

理解接口调用和实现职责后，再逐步补业务错误处理、查询业务与 Controller。当前仍是基础用户数据模块，不是登录模块。
