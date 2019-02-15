package com.Dyson.everything.core.dao;

import com.Dyson.everything.core.model.Condition;
import com.Dyson.everything.core.model.Thing;

import java.util.List;

/**
 * 业务层访问数据库的CRUD
 * @author Dyson
 * @date 2019/2/15 16:34
 */
public interface FileIndexDao {
    /**
     * 根据condition条件进行数据库检索
     * @param condition
     * @return
     */
    List<Thing> search(Condition condition);

    /**
     * 插入数据Thing
     * @param thing
     */
    void insert(Thing thing);

}
