package com.Dyson.everything.core.interceptor;

import java.io.File;

/**
 * 监视器,专门用作处理结果集，
 * @author Dyson
 * @date 2019/2/15 20:33
 */

//添加函数式接口注解，方便lambda表达式书写
@FunctionalInterface
public interface FileInterceptor {
    void apply(File file);
}
