package com.example.utils;

import io.lettuce.core.Limit;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * redis限流工具
 */
@Slf4j
@Component
public class FlowUtils {

    @Resource
    StringRedisTemplate template;

    /**
     * 针对于单次频率限制，请求成功后，在冷却时间内不得再次进行请求，如3秒内不能再次发起请求
     *
     * @param key       键
     * @param blockTime 阻塞时间
     * @return 是否提供检查
     */
    public Boolean limitOnceCheck(String key, int blockTime) {
        return this.internalCheck(key, 1, blockTime, (overclock) -> false);
    }


    /**
     * 针对在时间段内多次请求限制
     *
     * @param counterKey 计数键
     * @param blockKey   封禁键
     * @param blockTime  封禁时间
     * @param frequency  请求频率
     * @param period     计数周期
     * @return 是否通过限流检查
     */
    public boolean limitPeriodCheck(String counterKey, String blockKey, int blockTime, int frequency, int period) {
        return this.internalCheck(counterKey, frequency, period, (overclock) -> {
            if (overclock) {
                template.opsForValue().set(blockKey, "", blockTime, TimeUnit.SECONDS);
            }
            return !overclock;
        });
    }

    /**
     * 内部使用请求限制主要逻辑
     *
     * @param key       计数键
     * @param frequency 请求频率
     * @param period    计数周期
     * @param action    限制行为与策略
     * @return 是否通过限流检查
     */
    private boolean internalCheck(String key, int frequency, int period, LimitAction action) {
        String count = template.opsForValue().get(key);
        if (count != null) {
            long increment = Optional.ofNullable(template.opsForValue().increment(key)).orElse(0L);
            int c = Integer.parseInt(count);
            if (increment != c + 1) {
                template.expire(key, period, TimeUnit.SECONDS);
            }
            return action.run(increment > frequency);
        } else {
            template.opsForValue().set(key, "1", period, TimeUnit.SECONDS);
            return true;
        }
    }

    /**
     * 限制行为与策略（用法不是很清楚）
     */
    private interface LimitAction {
        boolean run(boolean overclock);
    }

}
