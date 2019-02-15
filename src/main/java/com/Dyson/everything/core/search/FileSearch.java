package com.Dyson.everything.core.search;

import com.Dyson.everything.core.dao.DataSourceFactory;
import com.Dyson.everything.core.dao.imp.FileIndexDaoImpl;
import com.Dyson.everything.core.model.Condition;
import com.Dyson.everything.core.model.Thing;
import com.Dyson.everything.core.search.impl.FileSearchImpl;

import java.util.List;

/**
 * 检索
 * @author Dyson
 * @date 2019/2/15 9:33
 */
//面向接口编程
public interface FileSearch {
    //TODO

    /**
     * 根据condition条件进行数据库检索
     * @param condition
     * @return
     */
    List<Thing> search(Condition condition);

}
