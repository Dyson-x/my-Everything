package com.Dyson.everything.core.dao;

import com.alibaba.druid.pool.DruidDataSource;

import javax.activation.DataSource;
import java.io.File;
import java.io.InputStream;

/**
 * @author Dyson
 * @date 2019/2/14 12:18
 */
public class DataSourceFactory {
    private static volatile DruidDataSource dataSource;
    private DataSourceFactory(){
    }
    public static DruidDataSource dataSource(){
        if(dataSource == null){
            synchronized (DataSourceFactory.class){
                if(dataSource == null){
                    //实例化
                    dataSource = new DruidDataSource();
                    dataSource.setDriverClassName("org.h2.Driver");
                    //url，usename，password
                    //采用H2的嵌入式数据库，数据库一本地文件文件的方式存储，只需要提高url接口
                    String workDir=System.getProperty("user.dir");
                    dataSource.setUrl("jdbc:h2:"+ workDir + File.separator + "my_everything");
                }
            }
        }
        return dataSource;
    }
    public static void initDatabase(){
        InputStream in=DataSourceFactory.class.getClassLoader().getResourceAsStream("my_everything.sql");

    }

    public static void main(String[] args) {
        DataSourceFactory.dataSource();

    }
}
