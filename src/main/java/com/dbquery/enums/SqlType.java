package com.dbquery.enums;

/**
 * @author yuanguangxin
 */

public enum SqlType {
    /**
     * 查询 SQL
     */
    SELECT(0),
    /**
     * insert SQL
     */
    INSERT(1),
    /**
     * delete SQL
     */
    DELETE(2),
    /**
     * update SQL
     */
    UPDATE(3);

    private final Integer value;

    SqlType(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }

    public Integer getValue() {
        return value;
    }
}
