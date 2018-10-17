package com.runjf.mybatis.config;

import com.runjf.mybatis.interceptor.SpringJdbcResultSetInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by rjf on 2018/10/14.
 */
@Configuration
public class MybatisCrudHelperConfig {

    @Bean
    @ConditionalOnMissingBean
    public SpringJdbcResultSetInterceptor springJdbcResultSetInterceptor() {
        return new SpringJdbcResultSetInterceptor();
    }

}
