package com.runjf.mybatis.crud;

import org.mybatis.dynamic.sql.SqlTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 用于Dao层继承，提供基础操作
 *
 * Created by rjf on 2018/5/12.
 */
public abstract class AbstractBaseMapperDao<D extends BaseMapper<T, ID>, T extends Identity<ID>, ID> implements PagingAndSortingRepository<T, ID> {

    private final D mapper;

    public AbstractBaseMapperDao(D mapper) {
        this.mapper = mapper;
    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        return mapper.selectByExample()
                .orderBy(PageUtils.buildSortSpecifications(sort))
                .build()
                .execute();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return PageUtils.buildPage(pageable, orders -> mapper.selectByExample()
                .orderBy(orders)
                .build()
                .execute(), getSqlTable());
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Override
    public <S extends T> S save(S entity) {
        if (entity != null) {
            int count;
            if (entity.getId() == null) {
                count = mapper.insert(entity);
            } else {
                count = mapper.updateByPrimaryKeySelective(entity);
            }
            if (count == 1) {
                return (S) mapper.selectByPrimaryKey(entity.getId());
            }
        }
        return null;
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        List<S> savedEntities = new ArrayList<>();
        entities.forEach(e -> savedEntities.add(save(e)));
        return savedEntities;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(mapper.selectByPrimaryKey(id));
    }

    @Override
    public boolean existsById(ID id) {
        return mapper.existsByPrimaryKey(id);
    }

    @Override
    public Iterable<T> findAll() {
        return mapper.selectByExample().build().execute();
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        if (ids instanceof List) {
            return mapper.selectAllByPrimaryKey((List<ID>) ids);
        }
        List<ID> list = new ArrayList<>();
        ids.forEach(list::add);
        return mapper.selectAllByPrimaryKey(list);
    }

    @Override
    public long count() {
        return mapper.countByExample().build().execute();
    }

    @Override
    public void deleteById(ID id) {
        mapper.deleteByPrimaryKey(id);
    }

    @Override
    public void delete(T entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        List<ID> list = new ArrayList<>();
        entities.forEach(e -> list.add(e.getId()));
        mapper.deleteAllByPrimaryKey(list);
    }

    @Override
    public void deleteAll() {
        mapper.deleteByExample().build().execute();
    }

    protected SqlTable getSqlTable() {
        return null;
    }

}
