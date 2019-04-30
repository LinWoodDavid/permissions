package com.david.configurer;

import com.alibaba.fastjson.JSON;
import com.david.core.Result;
import com.david.core.ResultCode;
import com.david.core.ResultGenerator;
import com.david.core.ResultMessage;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * =================================
 * Created by David on 2018/11/19.
 * mail:    17610897521@163.com
 * 描述:      权限拦截器
 */

public class SecurityInterceptor implements HandlerInterceptor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //验证签名
        boolean b = true;
        //boolean b = validateSign(request);// 一个简单签名验证
        if (b) {//签名认证成功
            //验证权限
            if (annotationsExist(handler)) {//存在权限注解,进行权限验证
                String token = request.getHeader("token");//获得token
                if (StringUtils.isNotBlank(token)) {//token不为空
                    /**
                     * 业务逻辑判断
                     * 此处写验证操作根据各自的业务需求进行验证,一般token会对应用户id,登录IP等信息
                     * 用户权限数据经常会用到建议缓存在redis中
                     */
                    return true;
                }
                //没有访问权限
                logger.warn("权限认证失败，请求接口：{}，请求IP：{}，请求参数：{}",
                        request.getRequestURI(), getIpAddress(request), JSON.toJSONString(request.getParameterMap()));
                responseResult(response, ResultGenerator.genFailResult(ResultCode.NO_PERMISSIONS, ResultMessage.NO_PERMISSIONS));
                return false;
            } else {
                //权限注解不存在,不需要验证权限直接放行
                return true;
            }
        } else {
            logger.warn("签名认证失败，请求接口：{}，请求IP：{}，请求参数：{}",
                    request.getRequestURI(), getIpAddress(request), JSON.toJSONString(request.getParameterMap()));
            //签名认证失败响应给客户端
            responseResult(response, ResultGenerator.genFailResult(ResultCode.UNAUTHORIZED, ResultMessage.UNAUTHORIZED));
            return false;
        }
    }

    /**
     * 判断是否存在权限注解
     *
     * @param handler
     * @return
     */
    private boolean annotationsExist(Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取方法/类上的注解
            RequiredPermission requiredPermission = handlerMethod.getMethod().getAnnotation(RequiredPermission.class);
            if (requiredPermission == null) {
                //如果方法上的注解为空 则获取类的注解
                requiredPermission = handlerMethod.getMethod().getDeclaringClass().getAnnotation(RequiredPermission.class);
            }
            //requiredPermission.value() 注解值 @RequestMapping("value") 括号中的值
            //注解不为空    说明此接口要进行权限验证
            if (requiredPermission != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    /**
     * token刷新机制
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String authenticationId = request.getHeader("token");
        if (StringUtils.isNotBlank(authenticationId)) {
            redisTemplate.expire(authenticationId, 30, TimeUnit.MINUTES);
        }
    }

    /**
     * 一个简单的签名认证，规则：
     * 1. 将请求参数按ascii码排序
     * 2. 拼接为a=value&b=value...这样的字符串（不包含sign）
     * 3. 混合密钥（secret）进行md5获得签名，与请求的签名进行比较
     */
    private boolean validateSign(HttpServletRequest request) {
        String requestSign = request.getParameter("sign");//获得请求签名，如sign=19e907700db7ad91318424a97c54ed57
        if (StringUtils.isEmpty(requestSign)) {
            return false;
        }
        List<String> keys = new ArrayList<String>(request.getParameterMap().keySet());
        keys.remove("sign");//排除sign参数
        Collections.sort(keys);//排序

        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(key).append("=").append(request.getParameter(key)).append("&");//拼接字符串
        }
        String linkString = sb.toString();
        linkString = StringUtils.substring(linkString, 0, linkString.length() - 1);//去除最后一个'&'

        String secret = "key";//密钥，自己修改
        String sign = DigestUtils.md5Hex(linkString + secret);//混合密钥md5

        return StringUtils.equals(sign, requestSign);//比较
    }

    /**
     * 获得ip
     *
     * @param request
     * @return
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，那么取第一个ip为客户端ip
        if (ip != null && ip.indexOf(",") != -1) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }
        return ip;
    }

    private void responseResult(HttpServletResponse response, Result result) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(200);
        try {
            response.getWriter().write(JSON.toJSONString(result));
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }
}
