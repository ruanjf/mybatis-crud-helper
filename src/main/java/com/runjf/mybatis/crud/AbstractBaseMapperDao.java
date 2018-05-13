package com.runjf.mybatis.crud;

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
public abstract class AbstractBaseMapperDao<T extends Identity<ID>, ID> implements PagingAndSortingRepository<T, ID> {

    private final BaseMapper<T, ID> baseMapper;

    public AbstractBaseMapperDao(BaseMapper<T, ID> baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        return baseMapper.selectByExample()
                .orderBy(PageUtils.buildSortSpecifications(sort))
                .build()
                .execute();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return PageUtils.buildPage(pageable, orders -> baseMapper.selectByExample()
                .orderBy(orders)
                .build()
                .execute());
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Override
    public <S extends T> S save(S entity) {
        if (entity != null) {
            int count;
            if (entity.getId() == null) {
                count = baseMapper.insert(entity);
            } else {
                count = baseMapper.updateByPrimaryKeySelective(entity);
            }
            if (count == 1) {
                return (S) baseMapper.selectByPrimaryKey(entity.getId());
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
        return Optional.ofNullable(baseMapper.selectByPrimaryKey(id));
    }

    @Override
    public boolean existsById(ID id) {
        return baseMapper.existsByPrimaryKey(id);
    }

    @Override
    public Iterable<T> findAll() {
        return baseMapper.selectByExample().build().execute();
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        if (ids instanceof List) {
            return baseMapper.selectAllByPrimaryKey((List<ID>) ids);
        }
        List<ID> list = new ArrayList<>();
        ids.forEach(list::add);
        return baseMapper.selectAllByPrimaryKey(list);
    }

    @Override
    public long count() {
        return baseMapper.countByExample().build().execute();
    }

    @Override
    public void deleteById(ID id) {
        baseMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void delete(T entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        List<ID> list = new ArrayList<>();
        entities.forEach(e -> list.add(e.getId()));
        baseMapper.deleteAllByPrimaryKey(list);
    }

    @Override
    public void deleteAll() {
        baseMapper.deleteByExample().build().execute();
    }

}
