package com.david.web;

import com.david.core.Result;
import com.david.core.ResultCode;
import com.david.core.ResultGenerator;
import com.david.core.ResultMessage;
import com.david.service.RedisLocker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

/**
 * =================================
 * Created by David on 2019/4/30.
 * mail:    17610897521@163.com
 * 描述:
 */
@RestController
public class LockDemo {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    RedisLocker redisLocker;

    private ConcurrentHashMap<String, Integer> products = getProduct();

    private ConcurrentHashMap<String, Integer> getProduct() {
        ConcurrentHashMap<String, Integer> products = new ConcurrentHashMap<>();
        for (int i = 0; i < 10; i++) {
            String s = String.valueOf(i);
            products.put(s, 100);
        }
        return products;
    }

    /**
     * 模拟商品数量-1
     * http://localhost:8080/lockTest?productId=0
     * http://localhost:8080/lockTest?productId=1
     * http://localhost:8080/lockTest?productId=2
     * http://localhost:8080/lockTest?productId=3
     * http://localhost:8080/lockTest?productId=4
     * http://localhost:8080/lockTest?productId=5
     *
     * @param productId
     * @return
     */
    @RequestMapping("lockTest")
    public Result lockTest(String productId) {
        //获取锁超时过期时间 当前时间戳+3000  3秒后获取失败返回false
        String expireTime = String.valueOf(System.currentTimeMillis() + 3000);
        //获取锁
        boolean lock = redisLocker.lock(productId, expireTime, 5);
        if (lock) {
            try {
                Integer count = products.get(productId);
                count--;
                products.put(productId, count);
                //模拟业务处理耗时 2秒
                Thread.sleep(1500);
                //释放锁
                redisLocker.unLock(productId);
                return ResultGenerator.genSuccessResult(count);
            } catch (InterruptedException e) {
                logger.error("InterruptedException: {}", e);
                return ResultGenerator.genFailResult(ResultCode.INTERNAL_SERVER_ERROR, ResultMessage.INTERNAL_SERVER_ERROR);
            }
        } else {
            return ResultGenerator.genFailResult(ResultCode.FAIL, ResultMessage.FAIL);
        }
    }
}
