package com.zlikun.open.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zhanglikun
 * @version 1.0
 * @date 2018-09-10 16:17
 */
@Controller
public class DefaultController {

    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "Hello, This is a test data .";
    }

}
