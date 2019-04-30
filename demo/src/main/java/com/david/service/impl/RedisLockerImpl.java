package com.david.service.impl;

import com.david.service.RedisLocker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * =================================
 * Created by David on 2019/3/22.
 * mail:    17610897521@163.com
 * 描述:
 */
@Component
public class RedisLockerImpl implements RedisLocker {

    private static final Logger logger = LoggerFactory.getLogger(RedisLockerImpl.class);

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 加锁
     *
     * @param key
     * @param expireTime 获取锁超时过期时间
     * @param seconds    过期时间防止死锁
     * @return
     */
    @Override
    public boolean lock(String key, String expireTime, Integer seconds) {
        //定义布尔类型用来接收获取锁是否成功
        Boolean b = false;
        //定义长整型用来接收超时时间
        Long expire = null;
        while (!b) {
            //较新版本的包有这个方法
            //b = stringRedisTemplate.opsForValue().setIfAbsent(key, expireTime, seconds, TimeUnit.SECONDS);
            //如果没有上面的方法,则用此方法防止死锁发生
            b = redisTransaction(key, expireTime, seconds, TimeUnit.SECONDS);
            if (b) {//获取锁成功
                return true;//返回true
            } else {//获取锁失败
                if (expire == null) expire = Long.valueOf(expireTime);//当expire 为 null 获取超时时间戳
                long l = System.currentTimeMillis();
                if (l > expire) {
                    return false;//超时返回 false
                }
                //延时 100ms
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    logger.error("lock InterruptedException; key:{},expireTime:{},seconds:{} ;cause: {}", key, expireTime, seconds, e);
                    return false;
                }
            }
        }
        return b;
    }

    /**
     * 释放锁
     *
     * @param key
     * @return
     */
    @Override
    public boolean unLock(String key) {
        return stringRedisTemplate.delete(key);
    }

    /**
     * redis 事务demo
     *
     * @param key     key
     * @param value   value
     * @param timeout
     * @return
     */
    private Boolean redisTransaction(String key, String value, long timeout, TimeUnit unit) {
        //创建会话
        SessionCallback<Boolean> sessionCallback = new SessionCallback<Boolean>() {
            List<Object> exec = null;

            @Override
            @SuppressWarnings("unchecked")
            public Boolean execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                stringRedisTemplate.opsForValue().setIfAbsent(key, value);
                stringRedisTemplate.expire(key, timeout, unit);
                exec = operations.exec();
                if (exec.size() > 0) {
                    return (Boolean) exec.get(0);
                }
                return false;
            }
        };
        return stringRedisTemplate.execute(sessionCallback);
    }

}
