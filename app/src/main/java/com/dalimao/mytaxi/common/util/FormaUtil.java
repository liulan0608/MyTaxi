package com.dalimao.mytaxi.common.util;

import java.util.regex.Pattern;

/**
 * author: apple
 * created on: 2018/5/15 上午10:49
 * description:
 */
public class FormaUtil {
    /**
     * 检查手机号是否正确
     * @return
     */
    public static boolean checkMobile(String mobile){
        String regex = "(\\+\\d+)?1[34578]\\d{9}$";
        return Pattern.matches(regex,mobile);
    }
}
