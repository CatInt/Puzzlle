package com.whalespool.puzzlle.utils;

import java.util.regex.Pattern;

public class ExUtil {

    public static Boolean confirmName(String name) {

//        boolean matches = Pattern.matches("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$", phone);
        boolean matches = !name.isEmpty() && name.indexOf(" ") != 0;
        return matches;
    }

    //密码格式要求：长度为8-20的数字，下划线或字母
    public static Boolean confirmPwd(String pwd) {
        //密码长度为8-20的数字，下划线或字母
        boolean matches = Pattern.matches("[a-zA-Z_0-9]{8,20}", pwd);
        return matches;
    }


}
