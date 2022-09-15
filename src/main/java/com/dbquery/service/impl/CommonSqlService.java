package com.dbquery.service.impl;

import com.dbquery.mapper.DbSqlMapper;
import com.dbquery.enums.SqlType;
import com.dbquery.service.SqlService;
import com.dbquery.util.CsvUtil;
import com.dbquery.util.SqlResultBuilder;
import com.dbquery.util.SqlUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author yuanguangxin
 */

public abstract class CommonSqlService implements SqlService {

    @Resource
    private DbSqlMapper dbSqlMapper;

    @Override
    public void switchDb(long userId) {
    }

    @Override
    public void switchDb(int port) {
    }

    @Override
    public void switchDb(String url, String user, String pass) {
    }

    @Override
    public void download(HttpServletResponse response, List<LinkedHashMap<String, Object>> result) throws Exception {
        String fileName = "result_" + System.currentTimeMillis() + ".csv";
        response.setCharacterEncoding("gbk");
        response.setContentType("text/csv");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        if (CollectionUtils.isEmpty(result)) {
            CsvUtil.exportWithCsvPrinter(response, Lists.newArrayList(), Lists.newArrayList());
            return;
        }
        LinkedHashMap<String, Object> one = result.get(0);
        List<String> header = Lists.newArrayList(one.keySet());
        List<List<String>> contents = new ArrayList<>();
        for (LinkedHashMap<String, Object> map : result) {
            List<String> row = new ArrayList<>();
            for (Object obj : map.values()) {
                row.add(String.valueOf(obj));
            }
            contents.add(row);
        }
        CsvUtil.exportWithCsvPrinter(response, contents, header);
    }

    @Override
    public List<LinkedHashMap<String, Object>> doSqlAllPartition(Integer env, String sql, HttpSession session) {
        return Lists.newArrayList();
    }

    @Override
    public void download(HttpServletResponse response, String sql) throws Exception {
        if (!check(sql)) {
            throw new RuntimeException("Unsupported sql");
        }
        List<LinkedHashMap<String, Object>> result = dbSqlMapper.selectBySql(sql);
        download(response, result);
    }

    public List<LinkedHashMap<String, Object>> doSqlCommon(String sql, boolean needLimit) {
        SqlType sqlType = SqlUtil.getSqlType(sql);
        if (Objects.isNull(sqlType)) {
            return new SqlResultBuilder().buildUnsupportedError();
        }
        Long result;
        try {
            switch (sqlType) {
                case SELECT:
                    if (needLimit) {
                        sql = SqlUtil.addLimit(sql);
                    }
                    return doSelect(sql);
                case INSERT:
                    result = dbSqlMapper.insertBySql(sql);
                    break;
                case DELETE:
                    result = dbSqlMapper.deleteBySql(sql);
                    break;
                case UPDATE:
                    result = dbSqlMapper.updateBySql(sql);
                    break;
                default:
                    return new SqlResultBuilder().buildUnsupportedError();
            }
        } catch (Exception e) {
            if (Objects.isNull(e.getCause()) || StringUtils.isBlank(e.getCause().getMessage())) {
                return new SqlResultBuilder().buildUnknownError();
            }
            return new SqlResultBuilder().buildError(e.getCause().getMessage());
        }
        return new SqlResultBuilder().buildCommon(result);
    }

    private List<LinkedHashMap<String, Object>> doSelect(String sql) {
        try {
            List<LinkedHashMap<String, Object>> result = dbSqlMapper.selectBySql(sql);
            return new SqlResultBuilder().buildCommon(result);
        } catch (Exception e) {
            if (Objects.isNull(e.getCause()) || StringUtils.isBlank(e.getCause().getMessage())) {
                return new SqlResultBuilder().buildUnknownError();
            }
            return new SqlResultBuilder().buildError(e.getCause().getMessage());
        }
    }
}
