package jdbc.entity;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * user 表对应的实体类
 * @author junyangwei
 * @date 2021-10-24
 */
@Data
@ToString
public class User {
    /**
     * 用户 ID
     */
    private int id;
    /**
     * 帐号
     */
    private String username;
    /**
     * 密码（经过 MD5 加密）
     */
    private String password;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 状态 0无效 1有效
     */
    private int status;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 最新的更新时间
     */
    private LocalDateTime updateTime;
}
