package com.dbquery.service.impl;

import com.dbquery.datasource.DynamicDataSource;
import com.dbquery.util.SqlAsyncUtil;
import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author yuanguangxin
 */
@Service("sqlService2")
public class DB2SqlService extends CommonSqlService {

    private static final Integer PARTITION_NUM = 3;

    private static final String[] HOST_LIST = new String[]{"10.45.4.33", "10.45.4.34", "10.45.4.35"};

    private long getDBNo(Long userId) {
        return userId % PARTITION_NUM;
    }

    @Override
    public void switchDb(long userId) {
        long dbNo = getDBNo(userId);
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://" + HOST_LIST[(int) dbNo] + ":3306??useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        DynamicDataSource.setDataSource(dataSource);
    }

    @Override
    public void switchDb(int dbNo) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://" + HOST_LIST[dbNo] + ":3306??useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        DynamicDataSource.setDataSource(dataSource);
    }

    @Override
    public List<LinkedHashMap<String, Object>> doSql(String sql, boolean needLimit) {
        return doSqlCommon(sql, needLimit);
    }

    @Override
    public List<LinkedHashMap<String, Object>> doSqlAllPartition(Integer env, String sql, HttpSession session) {
        return SqlAsyncUtil.callAndGet("doSqlAllPartitionDb2", PARTITION_NUM, Lists.newArrayList(),
                this, env, sql, session);
    }
}
