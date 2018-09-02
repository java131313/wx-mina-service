package com.zlikun.open.controller;

import com.zlikun.open.constant.AppConsts;
import com.zlikun.open.dto.WxInfoDto;
import com.zlikun.open.service.WxInfoService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/9/2 16:41
 */
@Slf4j
@RestController
@RequestMapping("/mina")
public class MinaPayController {

    @Autowired
    private WxInfoService wxInfoService;
    @Autowired
    private OkHttpClient httpClient;

    /**
     * 统一下单URL
     */
    private String unifiedOrderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     * 1. 下单操作
     *
     * @return
     */
    @PostMapping("/order")
    public Object doOrder(String token, String goodsId) throws IOException {
        log.info("1. 下单，token = {}, goodsId = {}", token, goodsId);

        // 参数校验（略）
        // 登录检查
        WxInfoDto info = wxInfoService.getWxInfo(token);
        if (info == null) {
            // 未登录，暂不考虑该情况
        }
        // 库存检查（略）
        // 生成订单（商户端），这里模拟一下（商户端订单ID）
        String merchantOrder = "merchant:" + goodsId + ":" + System.currentTimeMillis();

        // 执行微信统一下单
        unifiedOrder(merchantOrder);

        return null;
    }

    /**
     * 统一下单
     * https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=9_1
     *
     * @param merchantOrder 商户订单号
     * @throws IOException
     */
    private void unifiedOrder(String merchantOrder) throws IOException {
        // 模拟一个随机字符串
        String nonceStr = "https://zlikun.com";
        Request request = new Request.Builder()
                .url(unifiedOrderUrl)
                .post(new FormBody.Builder()
                        .add("appid", AppConsts.APP_ID)
                        .add("mch_id", AppConsts.MCH_ID)
                        // 随机数生成算法：https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_3
                        .add("nonce_str", nonceStr)
                        // 签名生成算法：https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_3
                        .add("sign", sign())
                        .add("sign_type", "MD5")
                        .add("body", "天然矿物盐，无任何添加剂")
                        .add("out_trade_no", merchantOrder)
                        .add("total_fee", "120")
                        .add("spbill_create_ip", "127.0.0.1")
                        // 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
                        .add("notify_url", "http://wx.zlikun.com/mina/pay_notify")
                        // 交易类型：https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_2
                        .add("trade_type", "JSAPI")
                        .build())
                .build();

        Response response = httpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            String content = response.body().string();

            log.info("content = {}", content);
        }

    }

    /**
     * TODO 生成签名
     * https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_3
     *
     * @return
     */
    private String sign() {
        return null;
    }

    /**
     * 异步接收微信支付结果URL，URL不能带参数
     */
    @RequestMapping(value = "/pay_notify", method = {GET, POST})
    public void payNotify() {

    }


}
