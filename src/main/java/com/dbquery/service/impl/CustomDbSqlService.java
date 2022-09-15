package com.dbquery.service.impl;

import com.dbquery.datasource.DynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author yuanguangxin
 */
@Service("sqlService3")
public class CustomDbSqlService extends CommonSqlService {

    @Override
    public void switchDb(long userId) {
    }

    @Override
    public void switchDb(String url, String user, String pass) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://" + url + "?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true");
        dataSource.setUsername(user);
        dataSource.setPassword(pass);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        DynamicDataSource.setDataSource(dataSource);
    }

    @Override
    public List<LinkedHashMap<String, Object>> doSql(String sql, boolean needLimit) {
        return doSqlCommon(sql, needLimit);
    }
}
