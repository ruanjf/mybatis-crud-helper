package com.runjf.mybatis.interceptor;

import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import java.util.Map;

/**
 * @author rjf
 * Created on 2018/10/24
 */
public class RowMapperResultSelectStatementProvider implements SelectStatementProvider {

    private final SelectStatementProvider delegate;
    private final RowMapper<?> rowMapper;

    public RowMapperResultSelectStatementProvider(SelectStatementProvider delegate, Class<?> resultClass) {
        this(delegate, BeanPropertyRowMapper.newInstance(resultClass));
    }

    public RowMapperResultSelectStatementProvider(SelectStatementProvider delegate, RowMapper<?> rowMapper) {
        this.delegate = delegate;
        this.rowMapper = rowMapper;
    }

    @Override
    public Map<String, Object> getParameters() {
        return delegate.getParameters();
    }

    @Override
    public String getSelectStatement() {
        return delegate.getSelectStatement();
    }

    public RowMapper<?> getRowMapper() {
        return rowMapper;
    }
}
