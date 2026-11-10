# UserController 完整新增链路

源码：projects/01-web-foundation/src/main/java/com/qiubai/lab/web/controller/UserController.java。
本轮只实现 POST /api/users，不增加查询或登录。Controller调用UserService接口，业务实现仍在UserServiceImpl。

## 注解与方法

- @RestController：Spring MVC识别控制器，返回对象作为响应体；当前由Jackson序列化为JSON。
- @RequestMapping("/api/users")：类级公共路径。
- @RequiredArgsConstructor：Lombok为final UserService字段生成构造器，Spring注入受管对象/代理。
- @PostMapping：只匹配POST，没有额外子路径，完整路径就是/api/users。
- @RequestBody：将JSON反序列化为CreateUserRequest，无法读取时转到全局异常处理返回400。
- @Valid：在当前Spring MVC方法签名下触发DTO校验，失败不执行方法体。
- userService.create(request)：执行业务流程；异常继续传播，Controller不重复try/catch。
- ResponseEntity.status(HttpStatus.CREATED)：设置HTTP201。
- body(new ApiResponse<>(...))：设置统一成功响应，异常响应由GlobalExceptionHandler组装。

## 完整流程

HTTP JSON → DTO → 校验 → Controller → UserService接口 → UserServiceImpl事务 → Mapper → MySQL → 回读User → UserResponse → ApiResponse<UserResponse> → HTTP201 JSON。
浏览器地址栏通常发送GET，不能替代POST。当前GET /api/users返回405，按ID查询尚未实现。

## 实际验证与保留数据

Maven package成功。正式JAR的真实HTTP验证通过：新增201/SUCCESS、重复409/USERNAME_ALREADY_EXISTS、字段错误400/INVALID_ARGUMENT、损坏JSON400/INVALID_ARGUMENT。
新增保留ID=10，username=http_user_1110133754，nickname=HTTP练习用户，created_at=2026-11-10 13:37:55；此前ID4、ID6记录保留。
就绪验证脚本首次因PowerShell内置变量名冲突误判，已修正重启后验证通过；首次没有执行创建请求。

## Postman与运行状态

集合infra/postman/backend-lab.postman_collection.json，已解析验证，五个请求：首页404、新增201、重复409、非法字段400、损坏JSON400。
新增成功请求发送前自动生成用户名；每次成功保留数据。重复示例依赖本机已保留的learn_user_01，新环境需先建立对应记录。
只更新了集合文件，未操作Postman导入界面，需要重新导入查看更新。
应用保持在http://127.0.0.1:18080运行，本次观察PID24284；进程记录infra/local/web-foundation-process.json，日志infra/logs。PID只是快照，后续停启前核对路径、启动时间和端口归属。

## 下一步

先阅读Controller并发送Postman请求，对照数据库和响应。理解后再逐步增加按ID查询的业务处理、未找到异常与GET入口。

## 参考

- https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-requestmapping.html
- https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/requestbody.html
