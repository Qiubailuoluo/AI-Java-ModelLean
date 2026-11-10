# common、DTO 与 Data 注解

## 目录按职责组织

- common/ApiResponse.java：多个接口共用的响应外壳 code/message/data。
- dto/CreateUserRequest.java：新增用户接口的输入数据。
- dto/UserResponse.java：后续按需创建的用户输出数据，不等于统一响应外壳。
- entity/User.java：数据库记录的 Java 表示。

common 是本项目的目录约定，不是 Spring 强制要求。DTO 是数据传输对象，既可以表示请求，也可以表示响应中的业务数据。
统一响应在广义上也用于数据传输，但这里把跨接口共用的类放 common，避免与特定功能 DTO 混淆。

## 当前改动

CreateUserRequest 改为 @Data，其原有字段校验规则保留。
@Data 来自 Lombok，包含 getter、非 final 字段 setter、toString、equals/hashCode 和 RequiredArgsConstructor 的常用行为。
当前无 final 或 @NonNull 字段，因此生成无参构造器，不生成两个字段的全参构造器。它不会自动校验或保存数据库。
这是简单可变请求 DTO 的一种合理选择，并非所有 DTO 都必须使用 @Data。

ApiResponse 使用 @Getter 与 @AllArgsConstructor，三个字段均为 final；创建时一次赋值，只提供读取方法。
AllArgsConstructor 按字段声明顺序生成构造参数。final 约束字段引用不再重新赋值，不代表业务数据对象深度不可变。
不需要 setter、字段相等性比较或内容输出时，无需统一使用 @Data。

## 泛型 T

ApiResponse<T> 中 T 是数据类型占位符，例如 ApiResponse<String> 的 data 为 String；后续 ApiResponse<UserResponse> 的 data 为 UserResponse。
不同接口可以复用响应结构，同时保留编译期类型检查。
ApiResponse 不决定 HTTP 状态码，后续 Controller 或异常处理器仍需要设置 201、400、409 等状态。

## 参考

- https://projectlombok.org/features/Data
- https://projectlombok.org/features/constructor

## 实际验证

Maven compile 成功。javap 确认 ApiResponse 生成三参数构造器和三个 getter；CreateUserRequest 生成无参构造器、getter/setter、equals/hashCode、toString。替换为 Data 后，六组请求校验场景仍全部通过。
