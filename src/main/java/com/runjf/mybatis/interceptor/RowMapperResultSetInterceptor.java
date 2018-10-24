package com.runjf.mybatis.interceptor;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * 动态指定返回值
 *
 * @author rjf
 * Created on 2018/10/14
 */
@Intercepts({ @Signature(
        type = ResultSetHandler.class,
        method = "handleResultSets",
        args = {Statement.class}
) })
public class RowMapperResultSetInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        ResultSetHandler drs = (ResultSetHandler) invocation.getTarget();
        Field parameterHandler = ReflectionUtils.findField(drs.getClass(), "parameterHandler");
        if (parameterHandler != null) {
            ReflectionUtils.makeAccessible(parameterHandler);
            ParameterHandler ph = (ParameterHandler) parameterHandler.get(drs);
            if (ph.getParameterObject() instanceof RowMapperResultSelectStatementProvider) {
                RowMapperResultSelectStatementProvider parameterObject = (RowMapperResultSelectStatementProvider) ph.getParameterObject();

                RowMapper<?> rowMapper = parameterObject.getRowMapper();
                if (rowMapper != null) {
                    Statement stmt = (Statement) invocation.getArgs()[0];

                    ResultSet resultSet = null;
                    try {
                        resultSet = stmt.getResultSet();
                        //noinspection unchecked
                        return new RowMapperResultSetExtractor(rowMapper).extractData(resultSet);
                    } finally {
                        JdbcUtils.closeResultSet(resultSet);
                    }
                }
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
