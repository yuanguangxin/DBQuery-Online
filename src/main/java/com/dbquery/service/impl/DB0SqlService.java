package com.dbquery.service.impl;

import com.dbquery.datasource.DynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author yuanguangxin
 */
@Service("sqlService0")
public class DB0SqlService extends CommonSqlService {

    @Override
    public void switchDb(long userId) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://10.45.4.31:3306?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        DynamicDataSource.setDataSource(dataSource);
    }

    @Override
    public List<LinkedHashMap<String, Object>> doSql(String sql, boolean needLimit) {
        return doSqlCommon(sql, needLimit);
    }
}
