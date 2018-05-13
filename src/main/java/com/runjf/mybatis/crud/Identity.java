package com.runjf.mybatis.crud;

/**
 * 对象唯一标识
 *
 * Created by rjf on 2018/5/12.
 */
public interface Identity<T> {
    T getId();

    void setId(T id);
}
