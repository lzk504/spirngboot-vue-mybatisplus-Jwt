package com.example.entity.vo.responese;

import lombok.Data;

import java.util.Date;

@Data
public class AuthorizeVo {
    String username;
    String role;
    String token;
    Date expire;
}
