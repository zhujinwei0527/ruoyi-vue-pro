package cn.iocoder.yudao.module.mes.dal.redis.md.autocode;

import cn.iocoder.yudao.module.mes.dal.redis.RedisKeyConstants;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

/**
 * MES 编码规则的 Redis DAO
 *
 * @author 芋道源码
 */
@Repository
public class MesMdAutoCodeRedisDAO {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 递增序号（带循环）
     *
     * @param keySuffix key 后缀（不包含 prefix）
     * @param duration 过期时间
     * @param step 步长
     * @return 递增后的值
     */
    public Long increment(String keySuffix, Duration duration, Integer step) {
        // 递增值
        String key = RedisKeyConstants.AUTO_CODE + keySuffix;
        Long value = stringRedisTemplate.opsForValue().increment(key, step);

        // 设置过期时间
        if (duration != null) {
            stringRedisTemplate.expire(key, duration);
        }
        return value;
    }

}
