package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.DtoAccount;
import com.example.entity.vo.request.EmailRegisterVo;
import com.example.mapper.AccountMapper;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, DtoAccount> implements AccountService {

    @Value("${spring.web.verify.mail-limit}")
    int Limit;
    @Resource
    FlowUtils flowUtils;

    @Resource
    AmqpTemplate amqpTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    PasswordEncoder encoder;

    /**
     * 从数据库中通过用户名或邮箱查找用户详细信息
     *
     * @param username 用户名
     * @return 用户详细信息
     * @throws UsernameNotFoundException 如果用户未找到抛出异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        DtoAccount account = this.findAccountByUsernameOrEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return User
                .withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    /**
     * 根据用户名/邮箱找到对应账号
     *
     * @param text 用户名/邮箱
     * @return 用户信息
     */
    @Override
    public DtoAccount findAccountByUsernameOrEmail(String text) {
        return this.query()
                .eq("username", text).or()
                .eq("email", text)
                .one();

    }

    /**
     * 生成注册验证码存入Redis中，并将邮件发送请求提交到消息队列等待发送
     *
     * @param type  邮件类型
     * @param email 邮箱
     * @param ip    用户IP
     * @return 操作结果，null表示正常,如果错误返回错误原因
     */
    @Override
    public String registerEmailVerifyCode(String type, String email, String ip) {

        synchronized (ip.intern()) {
            if (!this.verifyLimit(ip))
                return "请求频繁，请稍后再试";
            Random random = new Random();
            int code = random.nextInt(899999) + 100000;
            Map<String, Object> data = Map.of("type", type, "email", email, "code", code);
            amqpTemplate.convertAndSend(Const.MQ_MAIL, data);
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        }
    }

    /**
     * 注册新用户
     * @param emailRegisterVo 用户注册信息表
     * @return 如果在注册过程中有错误返回错误信息
     */
    @Override
    public String registerAccount(EmailRegisterVo emailRegisterVo) {
        String username = emailRegisterVo.getUsername();
        String email = emailRegisterVo.getEmail();
        String key = Const.VERIFY_EMAIL_DATA + email;
        String code = stringRedisTemplate.opsForValue().get(key);
        if (code == null) {
            return "请先获取验证码";
        }
        if (!code.equals(emailRegisterVo.getCode())) {
            return "验证码错误";
        }
        if (this.existAccountByEmail(email)) {
            return "此电子邮件已被其他用户注册";
        }
        if (this.existAccountByUsername(username)) {
            return "改用户名已经被注册，请更换一个新的用户名";
        }
        String password = encoder.encode(emailRegisterVo.getPassword());
        emailRegisterVo.setPassword(password);
        DtoAccount account = new DtoAccount(null, username, password, email, "user", new Date());
        if (this.save(account)) {
            stringRedisTemplate.delete(key);
            return null;
        }else {
            return "内部错误，请联系管理员";
        }

    }

    /**
     * 判断注册用户的邮箱是否已存在
     *
     * @param email 邮件
     * @return false存在, true不存在
     */
    private boolean existAccountByEmail(String email) {
        return this.baseMapper.exists(Wrappers.<DtoAccount>query().eq("email", email));
    }

    /**
     * 判断注册用户的用户名是否已存在
     *
     * @param username 用户名
     * @return false存在, true不存在
     */
    private boolean existAccountByUsername(String username) {
        return this.baseMapper.exists(Wrappers.<DtoAccount>query().eq("username", username));
    }

    /**
     * 针对IP地址进行邮件验证码获取限流
     *
     * @param ip ip地址
     * @return 检测是否提供验证
     */
    private boolean verifyLimit(String ip) {
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        return flowUtils.limitOnceCheck(key, Limit);
    }
}
