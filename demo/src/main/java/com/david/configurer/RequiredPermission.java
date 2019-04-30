package com.david.configurer;

import java.lang.annotation.*;

/**
 * =================================
 * Created by David on 2018/11/19.
 * mail:    17610897521@163.com
 * 描述:      权限注解   配合拦截器结合使用 验证权限
 *      ElementType.TYPE，ElementType.METHOD表示注解可以标记类和方法
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RequiredPermission {

    String value();

}
