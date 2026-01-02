package com.sky.controller.nofity;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("Test01")
@Slf4j
@Api(tags = "工作台相关接口")
public class Test01 {
    @GetMapping ("test01")
    public String test01(String name) {
        return "Hello Hebe" + name;
    }
}
