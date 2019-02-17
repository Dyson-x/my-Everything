package com.Dyson.everything.core.interceptor.impl;

import com.Dyson.everything.core.dao.FileIndexDao;
import com.Dyson.everything.core.interceptor.ThingInterceptor;
import com.Dyson.everything.core.model.Thing;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Dyson
 * @date 2019/2/17 17:45
 */
public class ThingClearInterceptor implements ThingInterceptor, Runnable {

    private Queue<Thing> queue = new ArrayBlockingQueue<>(1024);

    //关于数据库的操作
    private final FileIndexDao fileIndexDao;

    public ThingClearInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(Thing thing) {
        this.queue.add(thing);
    }

    @Override
    public void run() {
        while (true) {
            Thing thing = this.queue.poll();
            fileIndexDao.delete(thing);
//            TODO  优化点：批量删除  扩展delete
//            List<Thing> thingList=new ArrayList<>();
        }
    }
}
