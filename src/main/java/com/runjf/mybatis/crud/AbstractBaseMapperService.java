package com.runjf.mybatis.crud;

import org.mybatis.dynamic.sql.SqlTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 用于Service层继承，提供基础操作
 *
 * Created by rjf on 2018/5/13.
 */
public abstract class AbstractBaseMapperService<D extends BaseMapper<T, ID>, T extends Identity<ID>, ID> implements BaseService<T, ID> {

    private final D mapper;
    private int sqlInLimit;

    public AbstractBaseMapperService(D mapper) {
        this(mapper, 500);
    }

    public AbstractBaseMapperService(D mapper, int sqlInLimit) {
        this.mapper = mapper;
        this.sqlInLimit = sqlInLimit;
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
    public List<T> getByIds(List<ID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<ID> list = removeDuplicate(ids);
        Collection<List<ID>> partition = partition(list, sqlInLimit);
        if (partition.size() == 1) {
            return mapper.selectAllByPrimaryKey(partition.iterator().next());
        } else {
            List<T> ret = new ArrayList<>(ids.size());
            for (List<ID> idList : partition) {
                ret.addAll(mapper.selectAllByPrimaryKey(idList));
            }
            return ret;
        }
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
            List<ID> list = removeDuplicate(Arrays.asList(ids));
            Collection<List<ID>> partition = partition(list, sqlInLimit);
            for (List<ID> idList : partition) {
                mapper.deleteAllByPrimaryKey(idList);
            }
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

    @Override
    public Page<T> getPageByParams(T params, Pageable pageable) {
        return PageUtils.buildPage(pageable, order -> mapper.selectByExampleWhereSelective(params)
                        .orderBy(order)
                        .build()
                        .execute(),
                getSqlTable());
    }

    protected D getMapper() {
        return this.mapper;
    }

    protected static <T> Collection<List<T>> partition(List<T> list, int size) {
        if (size <= list.size()) {
            return Collections.singleton(list);
        }
        final AtomicInteger counter = new AtomicInteger(0);

        return list.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / size))
                .values();
    }

    protected abstract SqlTable getSqlTable();

    private <V> List<V> removeDuplicate(List<V> list) {
        return list.stream().distinct().collect(Collectors.toList());
    }

}
