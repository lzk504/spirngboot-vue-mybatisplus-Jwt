package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 确认重置验证码
 */
@Data
@AllArgsConstructor
public class ConfirmResetVo {
    @Email
    String email;

    @Length(max = 6, min = 6)
    String code;
}
