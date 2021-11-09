package com.hu.oneclick.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xwf
 * @date 2021/11/9 19:05
 */
public class FileUtil {

    /**
     * 重命名文件
     * @param fileName
     * @return
     */
    public static String renameFile( String fileName) {
        SimpleDateFormat fmdate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String newFileName = fileName.split("\\.")[0]+fmdate.format(new Date())+"."+fileName.split("\\.")[1];
        return newFileName;
    }
}
