package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.DtoAccount;
import com.example.entity.vo.request.ConfirmResetVo;
import com.example.entity.vo.request.EmailRegisterVo;
import com.example.entity.vo.request.EmailResetVo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<DtoAccount>, UserDetailsService {
    DtoAccount findAccountByUsernameOrEmail(String text);

    String registerEmailVerifyCode(String type, String email, String ip);

    String registerAccount(EmailRegisterVo emailRegisterVo);

    String resetConfirm(ConfirmResetVo confirmResetVo);

    String resetEmailAccountPassword(EmailResetVo emailResetVo);
}
