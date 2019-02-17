package com.Dyson.everything.core.search;

import com.Dyson.everything.config.myEverythingConfig;
import com.Dyson.everything.core.dao.DataSourceFactory;
import com.Dyson.everything.core.dao.FileIndexDao;
import com.Dyson.everything.core.dao.imp.FileIndexDaoImpl;
import com.Dyson.everything.core.index.FileScan;
import com.Dyson.everything.core.index.impl.FileScanImpl;
import com.Dyson.everything.core.interceptor.impl.FileIndexInterceptor;
import com.Dyson.everything.core.interceptor.impl.FilePrintIntercetor;
import com.Dyson.everything.core.model.Condition;
import com.Dyson.everything.core.model.Thing;
import com.Dyson.everything.core.search.impl.FileSearchImpl;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dyson
 * @date 2019/2/16 18:34
 */
public class myEverythingManager {
    //单例模式创建控制器
    private static volatile myEverythingManager manager;

    private FileSearch fileSearch;
    private FileScan fileScan;

    //线程池
    private ExecutorService executorService;

    public myEverythingManager() {
        this.initComponent();
    }

    //初始化插件
    private void initComponent() {
        //数据源对象
        DataSource dataSource = DataSourceFactory.dataSource();
        //检出数据库
        initOrResetDatabase();
        //业务层对象
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);
        this.fileSearch = new FileSearchImpl(fileIndexDao);
        this.fileScan = new FileScanImpl();
        //添加拦截器
        //this.fileScan.interceptor(new FilePrintIntercetor());
        this.fileScan.interceptor(new FileIndexInterceptor(fileIndexDao));
    }
    //检查并初始化数据库
    private void initOrResetDatabase() {
        //获取当前目录
        //TODO

        DataSourceFactory.initDatabase();

    }

    //获取实例化对象
    public static myEverythingManager getInstance() {
        //双重检查
        if (manager == null) {
            synchronized (myEverythingManager.class) {
                if (manager == null) {
                    manager = new myEverythingManager();
                }
            }
        }
        return manager;
    }

    /**
     * 检索
     */
    public List<Thing> search(Condition condition) {
        return this.fileSearch.search(condition);
    }

    /**
     * 索引
     */
    public void buildIndex() {
        initOrResetDatabase();
        //获取遍历文件路径
        Set<String> directories = myEverythingConfig.getInstance().getIncludePath();
        if (this.executorService == null) {
            //创建线程池
            this.executorService = Executors.newFixedThreadPool(directories.size(), new ThreadFactory() {
                //原子操作，避免线程名称重复
                private final AtomicInteger theadId = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    //设置线程名称，线程安全
                    thread.setName("Thread-Scan" + theadId.getAndIncrement());
                    return thread;
                }
            });
        }
        final CountDownLatch countDownLatch = new CountDownLatch(directories.size());
        System.out.println("Build index start ...");
        for (String path : directories) {
            //lambda表达式
            this.executorService.submit(() -> {
                myEverythingManager.this.fileScan.index(path);
                //每当一个线程工作完成后，数目-1
                countDownLatch.countDown();
            });
        }

        /**
         * 阻塞，直至所有任务完成
         */
        //阻塞式等待
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Build index complete...");
    }
}
