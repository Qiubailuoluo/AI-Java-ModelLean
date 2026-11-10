# 01：用户基础接口（已验收归档）

实现 POST /api/users 和 GET /api/users/{id}，包含请求校验、数据库读写、Service接口/实现、事务、统一响应和全局异常。
模块范围和复盘见03-模块归档.md；这不是登录模块，不包含密码、Session、Redis或JWT。

| 接口 | 成功 | 失败 |
| --- | --- | --- |
| POST /api/users | 201 SUCCESS | 参数错误400、用户名重复409 |
| GET /api/users/{id} | 200 SUCCESS | ID非法400、用户不存在404 |

## 启动

使用Java17、Maven和本机MySQL。新环境先依次执行仓库infra/mysql下的建库与建表SQL；已有库表不要删除重建。
在当前项目目录的PowerShell中设置连接变量（实际值查看本机账户笔记）：

```powershell
$env:LAB_DB_USERNAME = '替换为本机练习用户名'
$env:LAB_DB_PASSWORD = '替换为本机练习密码'
mvn package
java -jar '.\target\web-foundation-0.0.1-SNAPSHOT.jar'
```

只影响当前终端与后续子进程，不修改机器级环境变量。本机也可用Maven3.9.9完整路径与仓库内缓存：

```powershell
& 'D:\My_download\java_MAVEN\apache-maven-3.9.9\bin\mvn.cmd' -B -ntp '-Dmaven.repo.local=D:\backend-lab\infra\data\maven-repository' package
```

默认地址http://127.0.0.1:18080。首页GET /返回404，浏览器地址栏的GET不能代替新增接口的POST。
Windows会占用运行中的JAR，重新打包前先核实并停止本练习应用。当前本机已有进程运行，勿重复占用18080。
进程快照见infra/local/web-foundation-process.json，日志见infra/logs；停启前核对实际进程路径、启动时间和端口归属，不照旧PID盲目操作。

## Postman

导入仓库infra/postman/backend-lab.postman_collection.json，共10个请求：

- 先02新增成功：自动生成用户名，保留数据并记录userId集合变量。
- 再06查询：使用该userId，预期200。
- 03重复创建：复用02生成的用户名，预期409。
- 其他请求演示字段/JSON错误、用户不存在、0、非数字、Long溢出等情况。

新环境不要假定本机示例ID4、6、10、15存在，先新增再查询。

## 验收

从仓库根目录，在PowerShell7中运行：

```powershell
.\scripts\Verify-WebFoundation.ps1
```

13项真实HTTP验收均已通过，归档结果见acceptance/2026-11-10.json。脚本每次保留一条新用户，不自动删除成功数据。当前没有JUnit测试套件。
笔记与源代码中的注释按学习步骤保留；历史笔记的“尚未实现”表示当时阶段，当前状态以本README与模块归档为准。
