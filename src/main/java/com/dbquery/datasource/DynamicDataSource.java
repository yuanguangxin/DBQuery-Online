package com.dbquery.datasource;

import com.dbquery.util.SpringUtils;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;

/**
 * @author yuanguangxin
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<DataSource> DATA_SOURCE = ThreadLocal.withInitial(() -> (DataSource) SpringUtils.getBean("dynamicDataSource"));

    public static void setDataSource(DataSource dataSource) {
        DynamicDataSource.DATA_SOURCE.set(dataSource);
    }

    public static DataSource getDataSource() {
        return DynamicDataSource.DATA_SOURCE.get();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return null;
    }

    @Override
    protected DataSource determineTargetDataSource() {
        return getDataSource();
    }

    public static void clear() {
        HikariDataSource dataSource = (HikariDataSource) DynamicDataSource.DATA_SOURCE.get();
        dataSource.close();
        DynamicDataSource.DATA_SOURCE.remove();
    }
}

