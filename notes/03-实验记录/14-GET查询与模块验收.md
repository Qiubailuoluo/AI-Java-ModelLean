# 14：GET 查询与模块验收

本轮新增的Controller方法：

```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<UserResponse>> findById(
        @PathVariable("id") Long id) {
    UserResponse data = userService.findById(id);
    return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "查询成功", data));
}
```

- GetMapping来自Spring MVC，只匹配GET；方法路径与类级路径拼成/api/users/{id}。
- 花括号是路径变量占位符，如/api/users/4中的4。
- PathVariable将同名路径变量传给参数，并尝试转成Long。
- userService.findById调用已讲解的查询业务。
- ResponseEntity.ok设置200；成功仍用统一ApiResponse。
- 非数字/超范围先在参数转换阶段失败，不会执行方法体；新增异常处理返回400。
- 0、负数可以转成Long，但在Service中被业务校验拒绝，同样返回400。

Maven package与13组完整HTTP验收通过，结果归档在projects/01-web-foundation/acceptance/2026-11-10.json。验收脚本在scripts/Verify-WebFoundation.ps1，可在PowerShell7复现。
完整范围及复盘见项目03-模块归档.md。模块01是新增和查询用户，不是登录。

当前应用监听127.0.0.1:18080，本次观察PID25112；以后不能照旧PID盲目停止进程，需核实路径、启动时间、端口归属。
Postman集合有10个请求；先02创建，再06查询，03复用创建的用户名验证冲突。仅更新了文件，未自动导入Postman应用。
本次新增并保留ID15用户accept_11ad77a19362。无成功数据清理。

用户已确认沿用本机Git作者署名。按授权在模块完成后整理首次提交与module-01-web-foundation-v1标签，再推送；具体推送结果以本轮Git输出和远程核验为准。
下一阶段只先讨论单实例Session登录需求，不一口气生成完整模块。
