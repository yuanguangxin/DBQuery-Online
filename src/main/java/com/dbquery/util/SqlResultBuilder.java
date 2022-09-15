package com.dbquery.util;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author yuanguangxin
 */
public class SqlResultBuilder {

    private final List<LinkedHashMap<String, Object>> sqlResult = Lists.newArrayList();

    private List<LinkedHashMap<String, Object>> buildOk(String msg) {
        LinkedHashMap<String, Object> defaultResultMap = new LinkedHashMap<>();
        defaultResultMap.put("message", msg);
        sqlResult.add(defaultResultMap);
        return sqlResult;
    }

    private List<LinkedHashMap<String, Object>> buildUpdateOk() {
        return buildOk("OK");
    }

    private List<LinkedHashMap<String, Object>> buildEmptyOk() {
        return buildOk("Empty set");
    }

    private List<LinkedHashMap<String, Object>> buildAffectedOk(long rows) {
        return buildOk("OK, Affected rows:" + rows);
    }

    public List<LinkedHashMap<String, Object>> buildUnknownError() {
        return buildError("未知异常");
    }

    public List<LinkedHashMap<String, Object>> buildUnsupportedError() {
        return buildError("Unsupported sql");
    }

    public List<LinkedHashMap<String, Object>> buildError(String msg) {
        LinkedHashMap<String, Object> defaultResultMap = new LinkedHashMap<>();
        defaultResultMap.put("ok", false);
        defaultResultMap.put("message", msg);
        sqlResult.add(defaultResultMap);
        return sqlResult;
    }

    public List<LinkedHashMap<String, Object>> buildCommon(List<LinkedHashMap<String, Object>> result) {
        if (CollectionUtils.isEmpty(result)) {
            return buildEmptyOk();
        }
        return result;
    }

    public List<LinkedHashMap<String, Object>> buildCommon(Long result) {
        if (result == null) {
            return buildUnknownError();
        }
        if (result.equals(0L)) {
            return buildUpdateOk();
        }
        return buildAffectedOk(result);
    }
}
