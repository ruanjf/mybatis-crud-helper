package com.runjf.mybatis.crud;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

/**
 * 用于Service层继承，提供基础操作
 *
 * Created by rjf on 2018/5/13.
 */
public abstract class AbstractBaseMapperService<T extends Identity<ID>, ID> implements BaseService<T, ID> {

    private final BaseMapper<T, ID> baseMapper;

    public AbstractBaseMapperService(BaseMapper<T, ID> baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public T create(T entity) {
        if (entity != null) {
            int count = baseMapper.insert(entity);
            if (count == 1) {
                return get(entity.getId());
            }
        }
        return null;
    }

    @Override
    public T get(ID id) {
        return baseMapper.selectByPrimaryKey(id);
    }

    @Override
    public T update(T entity) {
        int update = baseMapper.updateByPrimaryKeySelective(entity);
        if (update == 1) {
            return get(entity.getId());
        }
        return null;
    }

    @Override
    public void delete(ID[] ids) {
        if (ids != null) {
            baseMapper.deleteAllByPrimaryKey(Arrays.asList(ids));
        }
    }

    @Override
    public Page<T> getPage(Pageable pageable) {
        return PageUtils.buildPage(pageable, orders -> baseMapper.selectByExample()
                .orderBy(orders)
                .build()
                .execute());
    }
}
