package com.Dyson.everything.config;

import com.Dyson.everything.core.common.FileConvertThing;
import lombok.Getter;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Dyson
 * @date 2019/2/15 19:35
 */
@Getter
public class myEverythingConfig {
    private static volatile myEverythingConfig config;
    /**
     * 定义两个属性：1.索引文件的路径 2.排除索引文件的路径
     */
    private Set<String> includePath = new HashSet<>();
    private Set<String> excludePath = new HashSet<>();


    //TODO 可配置的参数在这里体现

    /**
     * H2数据库文件路径
     */
    private String h2IndexPath=System.getProperty("user.dir")+ File.separator+"my_everything";
    /**
     * 检索最大返回值数量
     */

    private myEverythingConfig() {

    }

    /**
     * 初始化默认的配置
     */
    private void initDefaultPathsConfig() {
        //遍历的目录与排除的目录
        //1.获取文件系统
        FileSystem fileSystem = FileSystems.getDefault();
        //集合:可迭代的<Path>  JDK1.7
        //遍历
        Iterable<Path> iterable = fileSystem.getRootDirectories();
        //使用lambda表达式
        iterable.forEach(path -> config.includePath.add(path.toString()));
        //排除
        // windows: C:\Windows  C:\Program Files  C:\Program Files (x86)  C:\ProgramData
        //linux:  /tmp  /etc   /root
        String osname = System.getProperty("os.name");
        if (osname.startsWith("Windows")) {
            config.getExcludePath().add("C:\\Windows");
            config.getExcludePath().add("C:\\Program Files");
            config.getExcludePath().add("C:\\Program Files (x86)");
        } else {
            config.getExcludePath().add("/tmp");
            config.getExcludePath().add("/etc");
            config.getExcludePath().add("/root");
        }
    }

    /**
     * 取得实例化对象
     * @return
     */
    public static myEverythingConfig getInstance() {
        if (config == null) {
            synchronized (myEverythingConfig.class) {
                if (config == null) {
                    config = new myEverythingConfig();
                    //由于需要添加配置，避免空指针异常，使用饿汉式创建实例化对象
                    config.initDefaultPathsConfig();
                }
            }
        }
        return config;
    }

//    public static void main(String[] args) {
//        FileSystem fileSystem = FileSystems.getDefault();
//        //集合:可迭代的<Path>  JDK1.7
//        Iterable<Path> iterable = fileSystem.getRootDirectories();
//        iterable.forEach(new Consumer<Path>() {
//            @Override
//            public void accept(Path path) {
//                System.out.println(path);
//            }
//        });
//        //获取当前系统名称
//        String osname = System.getProperty("os.name");
//        System.out.println(osname);
//
//
//        myEverythingConfig config = myEverythingConfig.getInstance();
//        System.out.println(config.getExcludePath());
//        System.out.println(config.getIncludePath());
//    }
}


