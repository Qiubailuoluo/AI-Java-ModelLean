package com.qiubai.lab.web.mapper;

import com.qiubai.lab.web.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户数据访问接口：只负责 SQL 与数据映射，不处理 HTTP 响应或业务提示。
 * 当前 SQL 较短，先使用 MyBatis 注解；后续遇到复杂 SQL 时再学习 XML Mapper。
 */
@Mapper // MyBatis：由 starter 扫描，为该接口注册代理对象，交给 Spring 管理。
public interface UserMapper {

    // #{} 从 User 属性取值并绑定为 SQL 参数，不将输入直接拼接成 SQL。
    // 省略 id 和 created_at，让数据库生成主键并填入默认创建时间。
    @Insert("INSERT INTO app_user (username, nickname) VALUES (#{username}, #{nickname})")
    // 获取数据库生成的主键，回填到传入对象的 id；不会回填 createdAt。
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user); // 返回影响的行数，不是新用户的 ID。

    // created_at 通过已开启的下划线转驼峰设置映射到 createdAt。
    @Select("SELECT id, username, nickname, created_at FROM app_user WHERE id = #{id}")
    User findById(@Param("id") Long id); // @Param 指定参数名；没有匹配记录时返回 null。
}
