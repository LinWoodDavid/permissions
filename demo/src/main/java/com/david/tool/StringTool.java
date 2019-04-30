package com.david.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * =================================
 * Created by David on 2019/2/22.
 * mail:    17610897521@163.com
 * 描述:
 */

public class StringTool {

    /**
     * 获取响应结果状态码
     *
     * @param result 响应内容
     *               例如: Result{code=200, message='SUCCESS', data={"k","v"}}";
     * @return
     */
    public static int extractStatusCode(String result) {
        Pattern pattern = Pattern.compile("[0-9]{3,6}");
        Matcher matcher = pattern.matcher(result);
        boolean b = matcher.find();
        if (b) {
            String code = matcher.group();
            return Integer.valueOf(code);
        }
        return 0;
    }

}
