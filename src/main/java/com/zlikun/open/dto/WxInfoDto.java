package com.zlikun.open.dto;

import lombok.Data;
import org.springframework.util.DigestUtils;

/**
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/9/2 17:00
 */
@Data
public class WxInfoDto {
    private String sessionKey;
    private String openId;
    private String unionId;

    /**
     * 计算Token值
     *
     * @return
     */
    public String getToken() {
        return DigestUtils.md5DigestAsHex((this.openId + ":" + this.sessionKey).getBytes());
    }
}
