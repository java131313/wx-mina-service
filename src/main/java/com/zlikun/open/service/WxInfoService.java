package com.zlikun.open.service;

import com.zlikun.open.dto.WxInfoDto;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 将WxInfo和Token管理抽取出来实现复用
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/9/2 17:00
 */
@Service
public class WxInfoService {

    // 仅供测试，根据 session_key + openid 生成 token，用于标识微信用户
    private final ConcurrentHashMap<String, WxInfoDto> storage = new ConcurrentHashMap<>();

    /**
     * @param info
     * @return
     */
    public String putWxInfo(WxInfoDto info) {
        String token = info.getToken();
        storage.put(token, info);
        return token;
    }

    public WxInfoDto getWxInfo(String token) {
        return storage.get(token);
    }

    public boolean hasToken(String token) {
        return storage.containsKey(token);
    }
}
