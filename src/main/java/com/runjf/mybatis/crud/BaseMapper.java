package com.runjf.mybatis.crud;

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

import java.util.List;

public interface BaseMapper<T, ID> {

    long count(SelectStatementProvider selectStatement);

    int delete(DeleteStatementProvider deleteStatement);

    int insert(InsertStatementProvider<T> insertStatement);

    T selectOne(SelectStatementProvider selectStatement);

    List<T> selectMany(SelectStatementProvider selectStatement);

    int update(UpdateStatementProvider updateStatement);

    QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample();

    DeleteDSL<MyBatis3DeleteModelAdapter<ID>> deleteByExample();

    int deleteByPrimaryKey(ID id);

    int insert(T record);

    int insertSelective(T record);

    QueryExpressionDSL<MyBatis3SelectModelAdapter<List<T>>> selectByExample();

    QueryExpressionDSL<MyBatis3SelectModelAdapter<List<T>>> selectDistinctByExample();

    T selectByPrimaryKey(ID id);

    UpdateDSL<MyBatis3UpdateModelAdapter<ID>> updateByExample(T record);

    UpdateDSL<MyBatis3UpdateModelAdapter<ID>> updateByExampleSelective(T record);

    int updateByPrimaryKey(T record);

    int updateByPrimaryKeySelective(T record);

    List<T> selectAllByPrimaryKey(List<ID> ids);

    int deleteAllByPrimaryKey(List<ID> ids);

    boolean existsByPrimaryKey(ID id);

    <R> QueryExpressionDSL<R>.QueryExpressionWhereBuilder applyWhereSelective(QueryExpressionDSL<R> queryExpressionDSL, T params);

    QueryExpressionDSL<MyBatis3SelectModelAdapter<List<T>>>.QueryExpressionWhereBuilder selectByExampleWhereSelective(T params);

}