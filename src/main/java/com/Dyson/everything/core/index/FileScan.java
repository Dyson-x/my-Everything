package com.Dyson.everything.core.index;

import com.Dyson.everything.core.dao.DataSourceFactory;
import com.Dyson.everything.core.dao.imp.FileIndexDaoImpl;
import com.Dyson.everything.core.index.impl.FileScanImpl;
import com.Dyson.everything.core.interceptor.FileInterceptor;
import com.Dyson.everything.core.interceptor.impl.FileIndexInterceptor;
import com.Dyson.everything.core.interceptor.impl.FilePrintIntercetor;
import com.Dyson.everything.core.model.Thing;

import java.nio.file.Path;

/**
 * 索引加拦截
 * @author Dyson
 * @date 2019/2/15 18:21
 */
/*
 * 面向接口编程，在实现类中增添了一个添加拦截器的方法，当将主函数中的测试代码提到
 * 接口中测试时，就会发现添加拦截器方法不能使用
 */
public interface FileScan {
    //建立索引

    /**
     * 遍历 path
     * @param path
     */
    void index(String path);

    /**
     * 遍历的拦截器
     * @param interceptor
     */
    void interceptor(FileInterceptor interceptor);

//    public static void main(String[] args) {
//        //测试
//        FileScanImpl scan = new FileScanImpl();
//        FileInterceptor printIntercetor = new FilePrintIntercetor();
//        scan.interceptor(printIntercetor);
//        FileIndexInterceptor fileIndexInterceptor=new FileIndexInterceptor(new FileIndexDaoImpl(DataSourceFactory.dataSource()));
//        scan.interceptor(fileIndexInterceptor);
//        scan.index("C:\\Users\\Administrator\\Desktop");
//
//    }
}
