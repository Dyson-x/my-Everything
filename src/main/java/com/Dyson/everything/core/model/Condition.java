package com.Dyson.everything.core.model;

import lombok.Data;

/**
 * @author Dyson
 * @date 2019/2/14 15:34
 */
@Data
public class Condition {
    private String name;
    private String fileType;
    private Integer limit;
    /**
     * 检索结果文件信息depth排序规则
     * 1.默认true -> asc
     * 2.false -> desc
     */
    private Boolean orderByAsc;
}
