package com.david.service;

/**
 * =================================
 * Created by David on 2019/3/21.
 * mail:    17610897521@163.com
 * 描述:      David 之分布式锁
 * 1.获取锁应该是在redis中保存（key-value），
 * 释放锁应该是删除（key-value）
 * 2.商品A的状态更新和商品A的状态更新是没影响的，如果只有一个锁，那么肯定会导致性能很差。
 * 所以应该针对每个商品都要一个自己的锁，这样就可以提高效率。锁可以使用商品Id这些唯一的字段标志。
 * 3.对于一个线程去获取锁，如果锁已经被别人获取了，就采用轮询的方法在指定时间内获取锁。
 * 4.对于一个线程获取了锁，由于其他原因导致锁无法释放，应采用了对锁设置有效时间，
 * 这样即使锁无法释放，在有效时间后其他线程也可以继续获取。
 */
public interface RedisLocker {

    /**
     * 加锁
     *
     * @param key
     * @param seconds
     * @return
     */
    boolean lock(String key, String value, Integer seconds);

    /**
     * 释放锁
     *
     * @param key
     * @return
     */
    boolean unLock(String key);


}
