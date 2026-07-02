package com.inspiration.island.util;

import cn.hutool.crypto.digest.DigestUtil;

public class PasswordUtil {
    // MD5加密
    public static String md5Encrypt(String pwd){
        return DigestUtil.md5Hex(pwd);
    }
}