package com.Dyson.everything.core.common;

import com.Dyson.everything.core.model.FileType;
import com.Dyson.everything.core.model.Thing;

import java.io.File;

/**
 * 辅助工具类:将file对象转为thing对象
 * @author Dyson
 * @date 2019/2/15 18:33
 */
public final class FileConvertThing {
    private FileConvertThing() {
    }

    public static Thing convert(File file) {
        Thing thing = new Thing();
        thing.setName(file.getName());
        thing.setPath(file.getAbsolutePath());
        thing.setDepth(computerFileDepth(file));
        thing.setFileType(compuetrFileType(file));
        return thing;
    }

    /**
     * 计算路径深度
     *
     * @param file
     * @return
     */
    private static int computerFileDepth(File file) {
        int dept = 0;
        String[] segments = file.getAbsolutePath().split("\\\\");
        dept = segments.length;
        return dept;
    }

    /**
     * 判断文件类型
     * @param file
     * @return
     */
    private static FileType compuetrFileType(File file) {
        if (file.isDirectory()) {
            return FileType.OTHER;
        }
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        //这里注意可能会出现下标访问越界
        if (index != -1 && index < fileName.length() - 1) {
            String extend=fileName.substring(index+1);
            return FileType.lookup(extend);
        }
        return FileType.OTHER;
    }
}
