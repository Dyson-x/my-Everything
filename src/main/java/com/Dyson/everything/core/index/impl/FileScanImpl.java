package com.Dyson.everything.core.index.impl;

import com.Dyson.everything.config.myEverythingConfig;
import com.Dyson.everything.core.dao.DataSourceFactory;
import com.Dyson.everything.core.dao.imp.FileIndexDaoImpl;
import com.Dyson.everything.core.index.FileScan;
import com.Dyson.everything.core.interceptor.FileInterceptor;
import com.Dyson.everything.core.interceptor.impl.FileIndexInterceptor;
import com.Dyson.everything.core.interceptor.impl.FilePrintIntercetor;
import com.Dyson.everything.core.model.Thing;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dyson
 * @date 2019/2/15 18:22
 */
public class FileScanImpl implements FileScan {
    private myEverythingConfig config = myEverythingConfig.getInstance();
    //定义一个属性，遍历完之后，方便调用拦截器
    private LinkedList<FileInterceptor> interceptors = new LinkedList<>();

    //通过路径，递归遍历路径下所有文件及文件夹
    @Override
    //管道机制  Scan -> File -> Interceptor  当采用递归调用时，收集数据需要将集合定义到外部去
    //不需要将数据收集起来处理，遍历一个处理一个
    public void index(String path) {
        File file = new File(path);
        //将文件存到集合中
        //List<File> fileList=new ArrayList<>();
        if (file.isFile()) {
            if (config.getExcludePath().contains(file.getParent())) {
                return;
            }
        } else {
            if (config.getExcludePath().contains(path)) {
                return;
            } else {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        index(f.getAbsolutePath());
                    }
                }
            }
        }
        //File Directory 不论当前遍历的是文件还是目录，结果集进行统一拦截处理
        for (FileInterceptor interceptor : this.interceptors) {
            interceptor.apply(file);
        }
    }

    @Override
    public void interceptor(FileInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }

    //面向接口编程 ： 不需要再这里添加拦截器
//    public void addFileInterceptor(FileInterceptor fileInterceptor) {
//        this.interceptors.add(fileInterceptor);
//    }


}