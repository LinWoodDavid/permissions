package com.david.service.impl;

import com.david.service.CacheTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * =================================
 * Created by David on 2019/4/30.
 * mail:    17610897521@163.com
 * 描述:      spring cache 测试
 * 测试 spring cache 使用变量 content 代替查询/更改数据库操作,所以此处用 @Component 注解
 */
@Component
public class CacheTestImpl implements CacheTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String content = "content";

    /**
     * 获取内容
     *
     * @return
     */
    @Override
    public String getContent() {
        logger.info("getContent content: {}", this.content);
        return this.content;
    }

    /**
     * 更新内容
     *
     * @param content
     */
    @Override
    public void updateContent(String content) {
        this.content = content;
        logger.info("updateContent content: {}", content);
    }

}
