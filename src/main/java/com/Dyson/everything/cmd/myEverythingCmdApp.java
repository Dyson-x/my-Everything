package com.Dyson.everything.cmd;

import com.Dyson.everything.core.model.Condition;
import com.Dyson.everything.core.model.Thing;
import com.Dyson.everything.core.search.myEverythingManager;

import java.util.List;
import java.util.Scanner;

import static java.lang.Thread.sleep;

/**
 * @author Dyson
 * @date 2019/2/14 15:29
 */
public class myEverythingCmdApp {
    //阻塞式获取输入
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        //调度处理

        //欢迎界面
        welcome();

        //通过统一调度器调度
        //减少耦合，方便代码修改与管理
        myEverythingManager manager = myEverythingManager.getInstance();

        //交互式
        interactive(manager);

        System.out.println("这里是my-Everything应用程序的命令行交互程序");

    }

    /**
     * 处理控制台输入
     */
    private static void interactive(myEverythingManager manager) {
        //不断获取处理命令，检索
        while (true) {
            System.out.print("Everything ->");
            //不断读取输入Enter之前的字符
            String input = scanner.nextLine();
            //优先处理search字符串
            //处理方式:将输入的字符串按照空格截断并存入到字符串数组中
            //按照规定处理格式 search fileName fileType 进行处理
            if (input.startsWith("search")) {
                String[] values = input.split(" ");
                if (values.length >= 2) {
                    if (!values[0].equals("search")) {
                        help();
                        continue;
                    }
                    //按照输入设置Condition信息
                    Condition condition = new Condition();
                    //设置检索信息名称
                    condition.setName(values[1]);
                    //设置检索信息类型
                    if (values.length >= 3) {
                        condition.setFileType(values[2].toUpperCase());
                    }
                    //检索输入信息
                    //TODO
                    search(manager,condition);
                    continue;
                } else {
                    help();
                    continue;
                }
            }
            switch (input) {
                case "help":
                    help();
                    break;
                case "quit":
                    quit();
                    return;
                case "index":
                    index(manager);
                    break;
                default:
                    help();
            }
        }
    }

    private static void welcome() {
        System.out.println("欢迎使用Everything...");
    }

    private static void index(myEverythingManager manager) {
        //通过匿名创建线程
        //方法引用
        new Thread(manager::buildIndex).start();
    }

    private static void quit() {
        System.out.println("正在退出...");
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private static void search(myEverythingManager manger,Condition condition) {
        //统一调度器中调度search
        //manger.search(condition);
        //输出搜索结果路径
        List<Thing> thingList=manger.search(condition);
        for(Thing thing:thingList){
            System.out.println(thing.getPath());
        }
    }

    private static void help() {
        System.out.println("命令列表：");
        System.out.println("退出：quit");
        System.out.println("帮助：help");
        System.out.println("索引：index");
        System.out.println("搜索：search <name> [<fileType>]");
    }

}
