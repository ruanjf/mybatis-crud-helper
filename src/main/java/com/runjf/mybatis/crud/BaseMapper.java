package com.runjf.mybatis.crud;

import com.runjf.mybatis.interceptor.RowMapperResultSelectStatementProvider;
import com.runjf.mybatis.interceptor.RowMapperResultSetInterceptor;
import org.mybatis.dynamic.sql.delete.DeleteDSL;
import org.mybatis.dynamic.sql.delete.MyBatis3DeleteModelAdapter;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.select.MyBatis3SelectModelAdapter;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.MyBatis3UpdateModelAdapter;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.mybatis.dynamic.sql.where.AbstractWhereDSL;

import java.util.List;
import java.util.function.Function;

public interface BaseMapper<T, ID> {

    /**
     * 内部使用，获取列表
     * <p>
     * 需开启{@link RowMapperResultSetInterceptor}插件
     *
     * @param selectStatement 查询语句
     * @param <E> 实体类型
     * @return 集合列表
     */
    <E> List<E> selectList(SelectStatementProvider selectStatement);

    /**
     * 获取列表
     * <p>
     * 需开启{@link RowMapperResultSetInterceptor}插件
     *
     * @param clazz 返回值实体类型
     * @param <E> 实体类型
     * @return 集合列表
     * @see #selectList(SelectStatementProvider)
     */
    default <E> Function<SelectStatementProvider, List<E>> selectList(Class<E> clazz) {
        return selectStatementProvider
                -> this.selectList(new RowMapperResultSelectStatementProvider(selectStatementProvider, clazz));
    }

    long count(SelectStatementProvider selectStatement);

    int delete(DeleteStatementProvider deleteStatement);

    int insert(InsertStatementProvider<T> insertStatement);

    T selectOne(SelectStatementProvider selectStatement);

    List<T> selectMany(SelectStatementProvider selectStatement);

    int update(UpdateStatementProvider updateStatement);

    QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample();

    DeleteDSL<MyBatis3DeleteModelAdapter<Integer>> deleteByExample();

    int deleteByPrimaryKey(ID id);

    int insert(T record);

    int insertSelective(T record);

    QueryExpressionDSL<MyBatis3SelectModelAdapter<List<T>>> selectByExample();

    QueryExpressionDSL<MyBatis3SelectModelAdapter<List<T>>> selectDistinctByExample();

    T selectByPrimaryKey(ID id);

    UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExample(T record);

    UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExampleSelective(T record);

    int updateByPrimaryKey(T record);

    int updateByPrimaryKeySelective(T record);

    List<T> selectAllByPrimaryKey(List<ID> ids);

    int deleteAllByPrimaryKey(List<ID> ids);

    boolean existsByPrimaryKey(ID id);

    <R> QueryExpressionDSL<R>.QueryExpressionWhereBuilder applyWhereSelective(QueryExpressionDSL<R> dsl, T params);

    <R> UpdateDSL<R>.UpdateWhereBuilder applyWhereSelective(UpdateDSL<R> dsl, T params);

    <R> DeleteDSL<R>.DeleteWhereBuilder applyWhereSelective(DeleteDSL<R> dsl, T params);

    <D extends AbstractWhereDSL<D>> D applyWhereSelective(D dsl, T params);

    QueryExpressionDSL<MyBatis3SelectModelAdapter<List<T>>>.QueryExpressionWhereBuilder selectByExampleWhereSelective(T params);

}