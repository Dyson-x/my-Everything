package com.Dyson.everything.core.interceptor.impl;

import com.Dyson.everything.core.common.FileConvertThing;
import com.Dyson.everything.core.dao.FileIndexDao;
import com.Dyson.everything.core.interceptor.FileInterceptor;
import com.Dyson.everything.core.model.Thing;

import java.io.File;

/**
 * 写入数据库 和dao有关
 * @author Dyson
 * @date 2019/2/15 21:09
 */
public class FileIndexInterceptor implements FileInterceptor {
    private final FileIndexDao fileIndexDao;

    public FileIndexInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(File file) {
        Thing thing=FileConvertThing.convert(file);
//        System.out.println("Thing ==>"+thing);
        fileIndexDao.insert(thing);
    }
}
