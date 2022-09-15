package com.dbquery.controller;

import com.dbquery.common.CommonResponse;
import com.dbquery.datasource.DynamicDataSource;
import com.dbquery.enums.EnvEnum;
import com.dbquery.service.SqlService;
import com.dbquery.util.SqlUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yuanguangxin
 */
@RestController
@RequestMapping("/sql")
public class SqlController {

    private static final String EMPTY_SET = "Empty set";

    @Resource
    private Map<String, SqlService> sqlService;

    private SqlService getSqlService(Integer env) {
        EnvEnum envEnum = EnvEnum.of(env);
        if (Objects.isNull(envEnum)) {
            throw new RuntimeException("unknown env");
        }
        return sqlService.get("sqlService" + env);
    }

    private SqlService getOfflineSqlService(Integer env) {
        EnvEnum envEnum = EnvEnum.of(env);
        if (envEnum == EnvEnum.DB1) {
            return getSqlService(EnvEnum.DB0.value());
        }
        return null;
    }

    private void switchDb(SqlService sqlService, Long userId, String url, String user, String pass) {
        if (StringUtils.isBlank(url)) {
            sqlService.switchDb(userId);
            return;
        }
        sqlService.switchDb(url, user, pass);
    }

    private void saveUseSql(String sql, List<LinkedHashMap<String, Object>> result, Integer env, HttpSession session) {
        if (sql.toUpperCase().startsWith("USE") &&
                EMPTY_SET.equals(result.get(0).get("message"))) {
            session.setAttribute("dbname" + env, sql);
        }
    }

    private List<LinkedHashMap<String, Object>> insertToOffline(Integer env, String sql, Long userId, String url,
                                                                List<LinkedHashMap<String, Object>> result) {
        SqlService offLineService = getOfflineSqlService(env);
        if (Objects.isNull(offLineService)) {
            throw new RuntimeException("unknown env");
        }
        if (EMPTY_SET.equals(result.get(0).get("message"))) {
            return result;
        }
        switchDb(offLineService, userId, url, "", "");
        String insertSql = SqlUtil.buildInsert(sql, result);
        result = offLineService.doSql(insertSql, false);
        DynamicDataSource.clear();
        return result;
    }

    @PostMapping("/doSql")
    public CommonResponse doSql(@RequestParam("env") Integer env, @RequestParam("sql") String sql,
                                @RequestParam(required = false, defaultValue = "-1") Long userId,
                                @RequestParam(value = "url", required = false, defaultValue = "") String url,
                                @RequestParam(value = "user", required = false, defaultValue = "") String user,
                                @RequestParam(value = "pass", required = false, defaultValue = "") String pass,
                                @RequestParam(value = "allPartition", required = false, defaultValue = "false") Boolean allPartition,
                                @RequestParam(value = "insertToOffline", required = false, defaultValue = "false") Boolean insertToOffline,
                                HttpSession session) {
        SqlService sqlService = getSqlService(env);
        if (allPartition) {
            List<LinkedHashMap<String, Object>> result = sqlService.doSqlAllPartition(env, sql, session);
            return CommonResponse.ok(result);
        }
        switchDb(sqlService, userId, url, user, pass);
        sqlService.doUseSql(env, session);
        List<LinkedHashMap<String, Object>> result = sqlService.doSql(sql, true);
        saveUseSql(sql, result, env, session);
        DynamicDataSource.clear();
        if (insertToOffline) {
            result = insertToOffline(env, sql, userId, url, result);
        }
        return CommonResponse.ok(result);
    }

    @GetMapping("/download")
    public void download(@RequestParam("env") Integer env, @RequestParam("sql") String sql,
                         @RequestParam(required = false, defaultValue = "-1") Long userId,
                         @RequestParam(value = "url", required = false, defaultValue = "") String url,
                         @RequestParam(value = "user", required = false, defaultValue = "") String user,
                         @RequestParam(value = "pass", required = false, defaultValue = "") String pass,
                         @RequestParam(value = "allPartition", required = false, defaultValue = "false") Boolean allPartition,
                         HttpServletResponse response,
                         HttpSession session) throws Exception {
        SqlService sqlService = getSqlService(env);
        if (allPartition) {
            if (!sqlService.check(sql)) {
                throw new RuntimeException("Unsupported sql");
            }
            sqlService.download(response, sqlService.doSqlAllPartition(env, sql, session));
            return;
        }
        switchDb(sqlService, userId, url, user, pass);
        sqlService.doUseSql(env, session);
        sqlService.download(response, sql);
        DynamicDataSource.clear();
    }

    @GetMapping("/switchCustomDb")
    public CommonResponse switchCustomDb(@RequestParam("env") Integer env, @RequestParam("url") String url,
                                         @RequestParam("user") String user, @RequestParam("pass") String pass) {
        SqlService sqlService = getSqlService(env);
        if (Objects.isNull(sqlService)) {
            return CommonResponse.notOk("unknown env");
        }
        sqlService.switchDb(url, user, pass);
        List<LinkedHashMap<String, Object>> result = sqlService.doSql("select 1", false);
        DynamicDataSource.clear();
        return CommonResponse.ok(result);
    }
}
