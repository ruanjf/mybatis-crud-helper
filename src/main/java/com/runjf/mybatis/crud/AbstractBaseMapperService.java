package com.runjf.mybatis.crud;

import org.mybatis.dynamic.sql.SqlTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

/**
 * 用于Service层继承，提供基础操作
 *
 * Created by rjf on 2018/5/13.
 */
public abstract class AbstractBaseMapperService<D extends BaseMapper<T, ID>, T extends Identity<ID>, ID> implements BaseService<T, ID> {

    private final D mapper;

    public AbstractBaseMapperService(D mapper) {
        this.mapper = mapper;
    }

    @Override
    public T create(T entity) {
        if (entity != null) {
            int count = mapper.insert(entity);
            if (count == 1) {
                return get(entity.getId());
            }
        }
        return null;
    }

    @Override
    public T get(ID id) {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public T update(T entity) {
        int update = mapper.updateByPrimaryKeySelective(entity);
        if (update == 1) {
            return get(entity.getId());
        }
        return null;
    }

    @Override
    public void delete(ID[] ids) {
        if (ids != null) {
            mapper.deleteAllByPrimaryKey(Arrays.asList(ids));
        }
    }

    @Override
    public Page<T> getPage(Pageable pageable) {
        return PageUtils.buildPage(pageable, orders -> mapper.selectByExample()
                .orderBy(orders)
                .build()
                .execute(),
                getSqlTable());
    }

    protected SqlTable getSqlTable() {
        return null;
    }

}
