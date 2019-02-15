package com.Dyson.everything.core.interceptor.impl;

import com.Dyson.everything.core.interceptor.FileInterceptor;

import java.io.File;

/**
 * 遍历的路径的监视器
 * @author Dyson
 * @date 2019/2/15 20:39
 */

public class FilePrintIntercetor implements FileInterceptor {

    @Override
    public void apply(File file) {
        System.out.println(file.getAbsolutePath());
    }
}
