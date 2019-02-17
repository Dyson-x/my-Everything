package com.Dyson.everything.core.interceptor;

import com.Dyson.everything.core.model.Thing;

import java.io.File;

/**
 * @author Dyson
 * @date 2019/2/17 17:44
 */
@FunctionalInterface
public interface ThingInterceptor {
    void apply(Thing thing);
}
