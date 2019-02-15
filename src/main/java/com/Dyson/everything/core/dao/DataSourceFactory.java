package com.Dyson.everything.core.dao;

import com.Dyson.everything.core.model.Condition;
import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Dyson
 * @date 2019/2/14 12:18
 */
public class DataSourceFactory {
    private static volatile DruidDataSource dataSource;

    private DataSourceFactory() {
    }

    /**
     * 单例模式创建数据源，双重检查，在多线程情况下不会出现线程竞争
     * 无法实例化对象
     * @return
     */
    public static DataSource dataSource() {
        if (dataSource == null) {
            synchronized (DataSourceFactory.class) {
                if (dataSource == null) {
                    //实例化
                    dataSource = new DruidDataSource();
                    //JDBC   Driver class  实现JavaAPI规范Driver一个类
                    //设置h12数据源的驱动名称，通过反射实例化一个对象
                    dataSource.setDriverClassName("org.h2.Driver");
                    //url，usename，password
                    //采用H2的嵌入式数据库，数据库一本地文件文件的方式存储，只需要提高url接口
                    //JDBC 规范中关于MySQL jdbc:mysql://ip:port/databaseName

                    //获取当前目录
                    String workDir = System.getProperty("user.dir");

                    //JDBC规范中关于h2  jbbc:h2:filepath ->存储到本地文件
                    //JDBC规范中关于h2  jdbc:h2:~/filepath ->存储到当前用户的home目录
                    //H2服务端模式：JDBC规范中关于h2  jbbc:h2://ip:port/databaseName ->存储到本地文件

                    //在嵌入式会自动将你指定的目录最后的文件名当做数据库的名字
                    //在建表的时候就可以将建表语句注释掉
                    dataSource.setUrl("jdbc:h2:" + workDir + File.separator + "my_everything");
                    dataSource.setTestWhileIdle(false);
                }
            }
        }
        return dataSource;
    }

    //嵌入式数据库在创建数据源时，最终呈现在系统中是以本地文件的方式
    /**
     * 数据源的初始化
     */
    public static void initDatabase() {
        //1.获取数据源
        DataSource dataSource = DataSourceFactory.dataSource();
        //可以通过文件的绝对路径，但是不采用读取绝对路径文件，这样只会识别本电脑上的路径


        //JDK1.7  使用try-with-resources自动关闭流

        //采取读取classpath路径下文件
        //当做一个类去读，在classPath下通过classLoder下载
        try (InputStream in = DataSourceFactory.class.getClassLoader().getResourceAsStream("my_everything.sql");) {
            //读取classpath路径下文件
            if (in == null) {
                //没有读到初始化数据库脚本
                throw new RuntimeException("Not read init database script please check it");
            }
            //2.获取SQL语句
            try (BufferedReader Reader = new BufferedReader(new InputStreamReader(in));) {
                StringBuilder sqlBuilder = new StringBuilder();
                String line = null;

                while ((line = Reader.readLine()) != null) {
                    if (!line.startsWith("--")) {
                        sqlBuilder.append(line);
                    }
                }
                String sql = sqlBuilder.toString();
                //JDBC编程
                //3.获取数据库连接，并创建命令
                try (Connection connection = dataSource.getConnection();
                     //采用预编译命令
                     PreparedStatement statement = connection.prepareStatement(sql);
                ) {
                    //4.执行SQL语句
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        DataSourceFactory.initDatabase();
        //System.out.println(DataSourceFactory.dataSource());
    }
}
