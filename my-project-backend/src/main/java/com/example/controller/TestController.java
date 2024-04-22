package com.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Tag(name = "测试校验相关", description = "测试。")
public class TestController {

    @GetMapping("/hello")
    @Operation(summary = "测试操作")
    public String test(){
        return "Hello 小米";
    }
}
