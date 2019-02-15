package com.Dyson.everything.core.dao.imp;

import com.Dyson.everything.core.dao.DataSourceFactory;
import com.Dyson.everything.core.dao.FileIndexDao;
import com.Dyson.everything.core.model.Condition;
import com.Dyson.everything.core.model.FileType;
import com.Dyson.everything.core.model.Thing;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 业务层数据库的CRUD具体实现
 *
 * @author Dyson
 * @date 2019/2/15 16:37
 */
public class FileIndexDaoImpl implements FileIndexDao {
    //fianl初始化，直接初始化，构造方法初始化，构造块初始化
    //采用构造方法初始化避免耦合，通过传入的dataSource数据源，如果Factory发生改变就不会受到影响
    private final DataSource dataSource;

    public FileIndexDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Thing> search(Condition condition) {
        List<Thing> things = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        //定义一个结果集
        ResultSet resultSet = null;
        try {
            //1.获取数据库连接
            connection = dataSource.getConnection();
            //2.准备SQL语句
            //自定义查询规则
            //name : like
            //fileType
            //limit ： limit offset
            //orderbyAsc ： order by
            //该方法存在于虚拟机栈中，在这里不会出现线程安全问题，使用StringBuffer作用在方法中，
            // 离开方法区后会被销毁，不会被多线程共享，所以不用使用StringBuffer,但是当StringBuilder
            // 放在类的属性上去时就可能会被多线程访问，必须使用StringBuffer
            StringBuilder sqlBuilder=new StringBuilder();
            sqlBuilder.append(" select name, path, depth, file_type from file_index ");
            sqlBuilder.append(" where ").
                    append(" name like '%").
                    append(condition.getName()).
                    append("%' ");
            //条件中数据库存的为大写，当你存入小写时将其转化为大写，避免查询出错
            if(condition.getFileType()!=null){
                sqlBuilder.append(" and file_type = '").
                        append(condition.getFileType().toUpperCase()).
                        append("' ");
            }
            sqlBuilder.append(" order by depth ").
                    append(condition.getOrderByAsc()?"asc":"desc").
                    append(" limit ").
                    append(condition.getLimit()).
                    append(" offset 0 ");
            System.out.println(sqlBuilder);
            //3.准备命令
            statement = connection.prepareStatement(sqlBuilder.toString());
            //4.采用预编译命令，需要设置参数

            //5.执行查询命令
            resultSet = statement.executeQuery();
            //6.处理结果
            while (resultSet.next()) {
                //将数据库的行记录变成 java中的对象（Thing）
                Thing thing = new Thing();
                thing.setName(resultSet.getString("name"));
                thing.setPath(resultSet.getString("path"));
                thing.setDepth(resultSet.getInt("depth"));
                String fileType = resultSet.getString("file_type");
                thing.setFileType(FileType.lookupByName(fileType));
                things.add(thing);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            releaseResource(resultSet,statement,connection);
        }
        return things;
    }

    @Override
    public void insert(Thing thing) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            //1.获取数据库连接
            connection = dataSource.getConnection();
            //2.准备SQL语句
            String sql = "insert into file_index(name, path, depth, file_type) values (?,?,?,?)";
            //3.准备命令
            statement = connection.prepareStatement(sql);
            //4.采用预编译命令，需要设置参数
            statement.setString(1, thing.getName());
            statement.setString(2, thing.getPath());
            statement.setInt(3, thing.getDepth());
            //将枚举名称存储进去
            statement.setString(4, thing.getFileType().name());
            //5.执行更新命令
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            releaseResource(null,statement,connection);
        }
    }
    //解决内部代码大量重复问题
    private void releaseResource(ResultSet resultSet, PreparedStatement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(DataSourceFactory.dataSource());
        DataSourceFactory.initDatabase();
        Thing thing = new Thing();
        thing.setName("Dyson-x");
        thing.setPath("D:\\a\\b\\c\\Dyson-x.ppt");
        thing.setDepth(4);
        thing.setFileType(FileType.DOC);
        fileIndexDao.insert(thing);

        Condition condition=new Condition();
        condition.setName("Dyso");
        condition.setLimit(2);
        condition.setOrderByAsc(true);
        //condition.setFileType("IMG");
        //接受查询内容
        List<Thing> things = fileIndexDao.search(condition);
        for (Thing t : things) {
            System.out.println(t);
        }
    }
}
