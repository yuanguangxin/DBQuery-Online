package com.dbquery.util;

import com.dbquery.enums.SqlType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuanguangxin
 */
public class SqlUtil {

    private static final List<String> SELECT_START_PRE = Lists.newArrayList("SELECT", "SHOW", "DESC", "EXPLAIN", "USE");
    private static final List<String> INSERT_START_PRE = Lists.newArrayList("INSERT");
    private static final List<String> DELETE_START_PRE = Lists.newArrayList("DELETE");
    private static final List<String> UPDATE_START_PRE = Lists.newArrayList("UPDATE", "CREATE", "ALTER", "TRUNCATE", "DROP", "FLUSH");

    private static final Map<SqlType, List<String>> SQL_TYPE_MAP = Maps.newHashMap();

    static {
        SQL_TYPE_MAP.put(SqlType.SELECT, SELECT_START_PRE);
        SQL_TYPE_MAP.put(SqlType.INSERT, INSERT_START_PRE);
        SQL_TYPE_MAP.put(SqlType.DELETE, DELETE_START_PRE);
        SQL_TYPE_MAP.put(SqlType.UPDATE, UPDATE_START_PRE);
    }

    public static SqlType getSqlType(String sql) {
        if (StringUtils.isBlank(sql)) {
            return null;
        }
        final String upperSql = sql.toUpperCase();
        for (SqlType key : SQL_TYPE_MAP.keySet()) {
            List<String> value = SQL_TYPE_MAP.get(key);
            for (String start : value) {
                if (upperSql.startsWith(start)) {
                    return key;
                }
            }
        }
        return null;
    }

    public static String addLimit(String sql) {
        if (!sql.toUpperCase().startsWith("SELECT")) {
            return sql;
        }
        boolean needLimit = true;
        sql = sql.replaceAll("\n", " ");
        sql = sql.replaceAll("\t", " ");
        String[] sqlSplit = sql.split(" ");
        for (String temp : sqlSplit) {
            if (temp.toUpperCase().equals("LIMIT")) {
                needLimit = false;
            }
        }
        if (needLimit) {
            if (sql.endsWith(";")) {
                sql = sql.substring(0, sql.length() - 1);
            }
            sql = sql + " LIMIT 100";
        }
        return sql;
    }

    public static String getTableName(String sql) {
        sql = sql.replaceAll("\n", " ");
        sql = sql.replaceAll("\t", " ");
        String[] sqlSplit = sql.split(" ");
        return sqlSplit[3];
    }

    public static String getColumns(List<LinkedHashMap<String, Object>> result) {
        LinkedHashMap<String, Object> one = result.get(0);
        StringBuilder columns = new StringBuilder();
        one.keySet().forEach(key -> columns.append("`").append(key).append("`").append(","));
        return columns.substring(0, columns.length() - 1);
    }

    public static String buildInsert(String sql, List<LinkedHashMap<String, Object>> result) {
        String insertSql = "INSERT into " + getTableName(sql) + "(" + getColumns(result) + ") values";
        for (LinkedHashMap<String, Object> line : result) {
            String valuesSql = "(";
            for (Object obj : line.values()) {
                if (obj instanceof Number) {
                    valuesSql = valuesSql + obj + ",";
                } else {
                    valuesSql = valuesSql + "'" + obj + "',";
                }
            }
            valuesSql = valuesSql.substring(0, valuesSql.length() - 1) + ")";
            insertSql = insertSql + valuesSql + ",";
        }
        return insertSql.substring(0, insertSql.length() - 1) + ";";
    }
}
