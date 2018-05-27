package com.runjf.mybatis.crud;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlCriterion;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.delete.DeleteDSL;
import org.mybatis.dynamic.sql.select.MyBatis3SelectModelAdapter;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.where.AbstractWhereDSL;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mybatis.dynamic.sql.SqlBuilder.and;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;

/**
 * 辅助Sql生成
 *
 * Created by rjf on 2018/5/26.
 */
public class SqlBuilderHelper {

    public static <R> QueryExpressionDSL<R>.QueryExpressionWhereBuilder applyWhereSelective(
            QueryExpressionDSL<R> dsl,Object params, SqlTable table) {

        SqlBuilderHelper.ListSqlCriterion list = SqlBuilderHelper.getSqlAndCriteria(params, table);
        return dsl.where(list.getFirst().column(), list.getFirst().condition(), list.getOther());

    }

    public static <R> UpdateDSL<R>.UpdateWhereBuilder applyWhereSelective(
            UpdateDSL<R> dsl, Object params, SqlTable table) {

        SqlBuilderHelper.ListSqlCriterion list = SqlBuilderHelper.getSqlAndCriteria(params, table);
        return dsl.where(list.getFirst().column(), list.getFirst().condition(), list.getOther());

    }

    public static <R> DeleteDSL<R>.DeleteWhereBuilder applyWhereSelective(
            DeleteDSL<R> dsl, Object params, SqlTable table) {

        SqlBuilderHelper.ListSqlCriterion list = SqlBuilderHelper.getSqlAndCriteria(params, table);
        return dsl.where(list.getFirst().column(), list.getFirst().condition(), list.getOther());

    }

    public static <D extends AbstractWhereDSL<D>> D applyWhereSelective(D dsl, Object params, SqlTable table) {
        SqlBuilderHelper.ListSqlCriterion list = SqlBuilderHelper.getSqlAndCriteria(params, table);
        return dsl.and(list.first.column(), list.first.condition(), list.getOther());
    }

    public static ListSqlCriterion getSqlAndCriteria(Object params, SqlTable table) {
        if (params == null) {
            throw new IllegalArgumentException("params is null");
        }
        if (table == null) {
            throw new IllegalArgumentException("table is null");
        }

        Map<String, Method> getMethods = Arrays.stream(params.getClass().getMethods())
                .filter(m -> !m.isBridge() && m.getParameterCount() == 0 &&
                        m.getName().startsWith("get") && !"getClass".equals(m.getName()))
                .collect(Collectors.toMap(Method::getName, m -> m));
        Field[] fields = table.getClass().getFields();
        List<SqlCriterion<Object>> list = new ArrayList<>(fields.length);
        for (Field field : fields) {
            String name = field.getName();
            String getName = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
            Method method = getMethods.get(getName);
            if (method != null) {
                try {
                    Object value = method.invoke(params);
                    //noinspection unchecked
                    SqlColumn<Object> o = (SqlColumn<Object>) field.get(table);
                    SqlCriterion<Object> criterion = and(o, isEqualToWhenPresent(value));
                    list.add(criterion);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        if (list.isEmpty()) {
            throw new IllegalArgumentException("not found Getter Method in " + params.getClass());
        }
        return new ListSqlCriterion(list);
    }

    public static class ListSqlCriterion {
        private final SqlCriterion<Object> first;
        private final SqlCriterion<Object>[] other;
        private final List<SqlCriterion<Object>> list;

        ListSqlCriterion(List<SqlCriterion<Object>> list) {
            this.first = list.get(0);
            //noinspection unchecked
            this.other = list.size() > 1 ? list.subList(1, list.size()).toArray(new SqlCriterion[0]) : new SqlCriterion[0];
            this.list = list;

        }

        public SqlCriterion<Object> getFirst() {
            return first;
        }

        public List<SqlCriterion<Object>> getList() {
            return list;
        }

        public SqlCriterion<Object>[] getOther() {
            return other;
        }
    }

}
