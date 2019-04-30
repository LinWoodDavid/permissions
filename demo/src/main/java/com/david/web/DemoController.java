package com.david.web;

import com.david.configurer.RequiredPermission;
import com.david.core.Result;
import com.david.core.ResultGenerator;
import com.david.service.CacheTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * =================================
 * Created by David on 2019/4/30.
 * mail:    17610897521@163.com
 * 描述:
 */
@RestController
public class DemoController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    CacheTest cacheTest;

    /**
     * 不加权限注解可以直接访问
     * http://localhost:8080/hello
     * {"code":20000,"data":"hello","message":"SUCCESS"}
     *
     * @return
     */
    @RequestMapping("hello")
    public Result hello() {
        return ResultGenerator.genSuccessResult("hello");
    }

    /**
     * 无响应内容日志测试
     */
    @RequestMapping("demo")
    public void demo() {
        logger.info("demo come in");
    }

    /**
     * 加权限注解的会核对权限
     * http://localhost:8080/permission
     * {"code":40003,"message":"NO_PERMISSIONS"}
     *
     * @return
     */
    @RequiredPermission("permission")
    @RequestMapping("permission")
    public Result permission() {
        return ResultGenerator.genSuccessResult("permission");
    }

    //spring cache 测试
    /*
    访问两次http://localhost:8080/getContent
    查看日志可以发现:第一次将访问内容缓存到了redis中第二次直接在redis中获取并未打印日志"getContent content: content"

    访问http://localhost:8080/updateContent?content=新内容之后再次访问http://localhost:8080/getContent
    发现返回结果已经由"content" 变为 "新内容"

    下方为四次访问的日志:
    INFO 15556 --- [nio-8080-exec-3] com.david.configurer.RequestLog          : 请求接口：getContent，请求参数：[]
    INFO 15556 --- [nio-8080-exec-3] com.david.service.impl.CacheTestImpl     : getContent content: content
    INFO 15556 --- [nio-8080-exec-3] com.david.configurer.RequestLog          : =====>处理本次请求共耗时：4 ms
    INFO 15556 --- [nio-8080-exec-3] com.david.configurer.RequestLog          : 请求接口：getContent，响应参数为：{"code":20000,"data":"content","message":"SUCCESS"}
    INFO 15556 --- [nio-8080-exec-4] com.david.configurer.RequestLog          : 请求接口：getContent，请求参数：[]
    INFO 15556 --- [nio-8080-exec-4] com.david.configurer.RequestLog          : =====>处理本次请求共耗时：57 ms
    INFO 15556 --- [nio-8080-exec-4] com.david.configurer.RequestLog          : 请求接口：getContent，响应参数为：{"code":20000,"data":"content","message":"SUCCESS"}
    INFO 15556 --- [nio-8080-exec-5] com.david.configurer.RequestLog          : 请求接口：updateContent，请求参数：[新内容]
    INFO 15556 --- [nio-8080-exec-5] com.david.service.impl.CacheTestImpl     : updateContent content: 新内容
    INFO 15556 --- [nio-8080-exec-5] com.david.configurer.RequestLog          : =====>处理本次请求共耗时：7 ms
    INFO 15556 --- [nio-8080-exec-5] com.david.configurer.RequestLog          : 请求接口：updateContent，响应参数为：{"code":20000,"message":"SUCCESS"}
    INFO 15556 --- [nio-8080-exec-6] com.david.configurer.RequestLog          : 请求接口：getContent，请求参数：[]
    INFO 15556 --- [nio-8080-exec-6] com.david.service.impl.CacheTestImpl     : getContent content: 新内容
    INFO 15556 --- [nio-8080-exec-6] com.david.configurer.RequestLog          : =====>处理本次请求共耗时：4 ms
    INFO 15556 --- [nio-8080-exec-6] com.david.configurer.RequestLog          : 请求接口：getContent，响应参数为：{"code":20000,"data":"新内容","message":"SUCCESS"}
     */

    /**
     * @return
     */
    @RequestMapping("getContent")
    public Result getContent() {
        String content = cacheTest.getContent();
        return ResultGenerator.genSuccessResult(content);
    }

    /**
     * 更新内容
     *
     * @param content
     * @return
     */
    @RequestMapping("updateContent")
    public Result updateContent(String content) {
        cacheTest.updateContent(content);
        return ResultGenerator.genSuccessResult();
    }


}
