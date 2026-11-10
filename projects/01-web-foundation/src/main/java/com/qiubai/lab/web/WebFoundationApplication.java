package com.qiubai.lab.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 启用自动配置，并默认扫描当前包及其子包中的 Spring 组件。
@SpringBootApplication
public class WebFoundationApplication {

    // Java 程序入口；args 接收启动时传入的命令行参数。
    public static void main(String[] args) {
        // 创建 Spring 应用上下文；当前引入 Web 依赖，会同时启动嵌入式 Web 服务器。
        SpringApplication.run(WebFoundationApplication.class, args);
    }
}
