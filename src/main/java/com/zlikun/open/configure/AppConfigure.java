package com.zlikun.open.configure;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/9/2 17:18
 */
@Configuration
public class AppConfigure {

    @Bean
    public OkHttpClient httpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(1500, TimeUnit.MILLISECONDS)
                .build();
    }

}
