package com.Dyson.everything.core.search.impl;

import com.Dyson.everything.core.dao.FileIndexDao;
import com.Dyson.everything.core.model.Condition;
import com.Dyson.everything.core.model.Thing;
import com.Dyson.everything.core.search.FileSearch;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * 业务层
 * @author Dyson
 * @date 2019/2/15 16:19
 */
public class FileSearchImpl implements FileSearch {

    private final FileIndexDao fileIndexDao;

    public FileSearchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao=fileIndexDao;
    }

    @Override
    public List<Thing> search(Condition condition) {
        //数据库处理逻辑
        return this.fileIndexDao.search(condition);

    }
}
