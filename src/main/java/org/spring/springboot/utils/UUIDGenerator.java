package org.spring.springboot.utils;

import java.util.UUID;

public class UUIDGenerator {
    /** 
     * 获得一个UUID 
     * @return String UUID 
     */ 
    public static String getUUID(){ 
        String s = UUID.randomUUID().toString(); 
        //去掉“-”符号
        return  s.replaceAll("-", "");
    } 
}
