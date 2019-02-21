package com.Dyson.everything.core.monitor;

import com.Dyson.everything.core.common.HandlePath;

/**
 * @author Dyson
 * @date 2019/2/21 12:00
 */
public interface FileWatch {
    /**
     * 监听启动
     */
    void start();

    /**
     * 监听目录
     */
    void monitor(HandlePath handlePath);
    /**
     * 监听停止
     */
    void stop();
}
