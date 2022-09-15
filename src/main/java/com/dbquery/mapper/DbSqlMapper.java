package com.dbquery.mapper;

import org.apache.ibatis.annotations.*;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author yuanguangxin
 */
public interface DbSqlMapper {
    @Select("${sql}")
    List<LinkedHashMap<String, Object>> selectBySql(@Param("sql") String sql);

    @Insert("${sql}")
    Long insertBySql(@Param("sql") String sql);

    @Delete("${sql}")
    Long deleteBySql(@Param("sql") String sql);

    @Update("${sql}")
    Long updateBySql(@Param("sql") String sql);
}

