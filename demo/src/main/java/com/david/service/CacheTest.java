package com.david.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * =================================
 * Created by David on 2019/4/30.
 * mail:    17610897521@163.com
 * 描述:      spring cache 测试
 * <p>
 * 添加缓存
 *
 * @Cacheable(value = "user" ,key = "targetClass.name + methodName +#p0")
 * 方法上已经加了@CacheConfig 注解此处value可以省略
 * <p>
 * 清空缓存
 * <p>
 * 1.清除一条缓存，key为要清空的数据
 * @CacheEvict(value="emp",key="#id") 2.方法调用后清空所有缓存
 * @CacheEvict(value="accountCache",allEntries=true) 3.方法调用前清空所有缓存
 * @CacheEvict(value="accountCache",beforeInvocation=true)
 */
@CacheConfig(cacheNames = {"CacheTest"})
public interface CacheTest {

    /**
     * 获取内容
     *
     * @return
     */
    @Cacheable()
    String getContent();

    /**
     * 更新内容
     *
     * @param content
     */
    @CacheEvict(allEntries = true)
    void updateContent(String content);
}
