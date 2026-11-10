# Lombok 注解：从手写方法到编译生成

## 当前改动

User.java 使用 @Getter 和 @Setter，替换已理解的四组手写访问方法。
pom.xml 增加 org.projectlombok:lombok，scope=provided；版本由 Spring Boot 父 POM 管理，当前解析为 1.18.46。
Java 17、非模块化工程，本轮编译可发现 Lombok 注解处理器，不需要额外编译插件配置。

## 注释与注解

//、/* */ 是帮助人阅读的注释；@Getter、@Setter 是提供给工具处理的注解。
Lombok 在编译期间生成访问方法，源文件不会自动出现这些方法文本，但编译后的 User.class 包含方法。
@Getter 默认生成 public 读取方法；@Setter 为符合条件的可写实例字段生成 public 赋值方法，不自动执行格式校验或数据库保存。

## @Data

组合 Getter、Setter、ToString、EqualsAndHashCode、RequiredArgsConstructor 的常用行为。
RequiredArgsConstructor 为未初始化的 final 字段和未初始化的 @NonNull 字段生成构造参数；不是所有字段构造器。
当前只需要访问方法，使用 Getter/Setter 即可。Data 还会改变对象内容输出和相等性判断，不应不加区分地给所有类使用。

## 实际验证

2026-11-10：Maven compile 成功。javap 检查编译后的 User，确认四个 getter 和四个 setter 均存在。没有新增业务接口或修改数据库。

## 后续流程

不固定按层的编写顺序；围绕一个小功能，先确定输入输出与验收，再根据依赖完成实现、验证和讲解。

## 参考

- https://projectlombok.org/features/GetterSetter
- https://projectlombok.org/features/Data
- https://projectlombok.org/setup/maven
