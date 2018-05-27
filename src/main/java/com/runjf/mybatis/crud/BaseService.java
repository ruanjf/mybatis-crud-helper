package com.runjf.mybatis.crud;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 提供基础的增删查改
 *
 * Created by rjf on 2018/5/13.
 */
public interface BaseService<T, ID> {

    T create(T entity);

    T get(ID id);

    List<T> getByIds(List<ID> ids);

    T update(T entity);

    void delete(ID... ids);

    Page<T> getPage(Pageable pageable);

    Page<T> getPageByParams(T params, Pageable pageable);

}
