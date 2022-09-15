package com.dbquery.service;

import com.dbquery.enums.SqlType;
import com.dbquery.util.SqlUtil;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author yuanguangxin
 */
public interface SqlService {

    void switchDb(long userId);

    void switchDb(int port);

    void switchDb(String url, String user, String pass);

    List<LinkedHashMap<String, Object>> doSql(String sql, boolean needLimit);

    List<LinkedHashMap<String, Object>> doSqlAllPartition(Integer env, String sql, HttpSession session);

    void download(HttpServletResponse response, String sql) throws Exception;

    void download(HttpServletResponse response, List<LinkedHashMap<String, Object>> result) throws Exception;

    default boolean check(String sql) {
        SqlType sqlType = SqlUtil.getSqlType(sql);
        return SqlType.SELECT == sqlType;
    }

    default void doUseSql(Integer env, HttpSession session) {
        Object useSql = session.getAttribute("dbname" + env);
        if (Objects.nonNull(useSql)) {
            doSql(useSql.toString(), false);
        }
    }
}
