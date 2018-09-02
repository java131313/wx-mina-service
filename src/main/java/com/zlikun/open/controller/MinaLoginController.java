package com.zlikun.open.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 微信小程序登录控制器（REST API）
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/9/2 12:18
 */
@Slf4j
@RestController
@RequestMapping("/mina")
public class MinaLoginController {

    @Autowired
    private ObjectMapper mapper;

    private OkHttpClient client;

    // 仅供测试，根据 session_key + openid 生成 token，用于标识微信用户
    private final ConcurrentHashMap<String, WxInfo> storage = new ConcurrentHashMap<>();

    @Data
    private static class WxInfo {
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

    @PostConstruct
    public void init() {
        client = new OkHttpClient.Builder()
                .connectTimeout(1500, TimeUnit.MILLISECONDS)
                .build();
    }

    @PreDestroy
    public void destroy() {

    }

    private String wxUrl = "https://api.weixin.qq.com/sns/jscode2session";
    private String appId = "wx2bf382e1bd59b866";
    private String appSecret = "f360720dce9aa1fe5674bec87452a452";
    private String grantType = "authorization_code";

    /**
     * 微信登录
     * https://developers.weixin.qq.com/miniprogram/dev/api/api-login.html
     *
     * @param code
     * @return
     */
    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public Object doLogin(String code) {

        // code = 061SfMWR1k5jV71BGzVR1uG2XR1SfMWo
        log.info("code = {}", code);

        // https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code
        // appid        wx2bf382e1bd59b866
        // appsecret    f360720dce9aa1fe5674bec87452a452

        Request request = new Request.Builder()
                .url(wxUrl)
                .post(new FormBody.Builder()
                        .add("appid", appId)
                        .add("secret", appSecret)
                        .add("js_code", code)
                        .add("grant_type", grantType)
                        .build())
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {

                String content = response.body().string();

                // content = {"session_key":"HZ2UFIR5OS98yYP1GKJSbg==","openid":"o3i0p6BNEVUVCok4Vl4QLljHjVbY"}
                log.info("content = {}", content);

                // 提取session_key和openid字段
                Map<String, String> data = mapper.readValue(content, Map.class);

                // data = {session_key=HZ2UFIR5OS98yYP1GKJSbg==, openid=o3i0p6BNEVUVCok4Vl4QLljHjVbY}
                log.info("data = {}", data);

                // 判断data容器中的字段
                // openid, session_key[, unionid]
                // errcode, errmsg
                if (data.get("openid") != null) {
                    WxInfo info = new WxInfo();
                    info.setOpenId(data.get("openid"));
                    info.setSessionKey(data.get("session_key"));
                    String token = info.getToken();
                    storage.put(token, info);
                    return token;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 对wx.getUserInfo()返回的用户数据进行校验
     * https://developers.weixin.qq.com/miniprogram/dev/api/signature.html
     *
     * @param token       登录凭证
     * @param signature   wx.getUserInfo()返回的签名
     * @param userRawInfo wx.getUserInfo()返回的用户信息原文
     * @return
     */
    @PostMapping("/verify_signature")
    public Object doSignature(String token, String signature, String userRawInfo) {
        // 获取session_key，这里暂时不考虑未登录这种情况
        String sessionKey = storage.get(token).getSessionKey();
        // 这里使用的是Guava提供的SHA算法工具类
        String signature2 = Hashing.sha1().hashBytes((userRawInfo + sessionKey).getBytes()).toString();
        log.info("session_key = {}, signature = {}, signature2 = {}" ,sessionKey, signature, signature2);
        // 比较两个签名一致性
        return signature.equals(signature2);
    }

    /**
     * 模拟一个业务API，接收Token用于认证
     *
     * @param token
     * @return
     */
    @GetMapping("/logic")
    public Object doLogic(String token) {
        Map<String, Object> data = new HashMap<>(4);
        if (storage.containsKey(token)) {
            data.put("status", 1);  // 表示正常
        } else {
            data.put("status", -1); // 表示未登录（认证）
        }
        return data;
    }

}
