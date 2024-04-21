package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 重置密码
 */
@Data
@AllArgsConstructor
public class EmailResetVo {

    @Email
    String email;

    @Length(max = 6, min = 6)
    String code;

    @Length(min = 6, max = 20)
    String password;
}
