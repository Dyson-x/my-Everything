package com.Dyson.everything.core.common;

import lombok.Data;

import java.util.Set;

/**
 * @author Dyson
 * @date 2019/2/21 12:04
 */
@Data
public class HandlePath {
    private Set<String> includePath;
    private Set<String> excludePath;
}
