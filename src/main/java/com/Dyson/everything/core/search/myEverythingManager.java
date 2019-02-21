package com.Dyson.everything.core.search;

import com.Dyson.everything.config.myEverythingConfig;
import com.Dyson.everything.core.common.HandlePath;
import com.Dyson.everything.core.dao.DataSourceFactory;
import com.Dyson.everything.core.dao.FileIndexDao;
import com.Dyson.everything.core.dao.imp.FileIndexDaoImpl;
import com.Dyson.everything.core.index.FileScan;
import com.Dyson.everything.core.index.impl.FileScanImpl;
import com.Dyson.everything.core.interceptor.ThingInterceptor;
import com.Dyson.everything.core.interceptor.impl.FileIndexInterceptor;
import com.Dyson.everything.core.interceptor.impl.ThingClearInterceptor;
import com.Dyson.everything.core.model.Condition;
import com.Dyson.everything.core.model.Thing;
import com.Dyson.everything.core.monitor.FileWatch;
import com.Dyson.everything.core.monitor.impl.FileWatchImpl;
import com.Dyson.everything.core.search.impl.FileSearchImpl;

import javax.sql.DataSource;
import java.io.File;
import java.util.EventListener;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    //Thing拦截器
    //清理删除的文件
    private ThingClearInterceptor thingClearInterceptor;
    private Thread backgroundClearThread=new Thread(this.thingClearInterceptor);
    private AtomicBoolean backgroundClearThreadStatus=new AtomicBoolean(false);

    /**
     * 文件监控
     */
    private FileWatch fileWatch;
    public myEverythingManager() {
        this.initComponent();
    }

    //初始化插件
    private void initComponent() {
        //数据源对象
        DataSource dataSource = DataSourceFactory.dataSource();
        //初始化数据库
        initOrResetDatabase();
        //业务层对象
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);
        this.fileSearch = new FileSearchImpl(fileIndexDao);
        this.fileScan = new FileScanImpl();
        //添加拦截器
        //this.fileScan.interceptor(new FilePrintIntercetor());
        this.fileScan.interceptor(new FileIndexInterceptor(fileIndexDao));
        this.thingClearInterceptor=new ThingClearInterceptor(fileIndexDao);
        this.backgroundClearThread.setName("Thing-thred-clear");
        //清理线程设置成守护线程
        this.backgroundClearThread.setDaemon(true);
        //文件监控对象
        this.fileWatch =new FileWatchImpl(fileIndexDao);
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
        //TODO  扩展
        //如果文件不存在将其清除，通过Stream流式方式  JDK8
        return this.fileSearch.search(condition)
                .stream().filter(thing -> {
            String path=thing.getPath();
            File file=new File(path);
            boolean flag=file.exists();
            if(!flag){
                //删除
                thingClearInterceptor.apply(thing);
            }
            return flag;
        }).collect(Collectors.toList());
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

    /**
     * 启动清理线程
     */
    public void startbackgroundClearThread(){
        if(this.backgroundClearThreadStatus.compareAndSet(false,true)){
            this.backgroundClearThread.start();
        }else {
            System.out.println("BackgroundClearThrad has start");
        }
    }

    /**
     * 启动文件系统监听
     */
    public void startFileSystemMonitor(){
        myEverythingConfig config=myEverythingConfig.getInstance();
        HandlePath handlePath=new HandlePath();
        handlePath.setIncludePath(config.getIncludePath());
        handlePath.setExcludePath(config.getExcludePath());
        this.fileWatch.monitor(handlePath);
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("文件系统监控启动");
                fileWatch.start();
            }
        }).start();
    }



    /**
     * 历史记录
     */
    public void starthistoryRecord(){

    }

    public void buildHistory() {

    }

}
