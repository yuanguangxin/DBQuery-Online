package com.dbquery.enums;

/**
 * @author yuanguangxin
 */

public enum EnvEnum {
    /**
     * DB0
     */
    DB0(0),
    /**
     * DB1
     */
    DB1(1),
    /**
     * DB2
     */
    DB2(2),
    /**
     * Custom DB
     */
    CUSTOM_DB(3),
    ;

    private final Integer value;

    EnvEnum(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }

    public Integer getValue() {
        return value;
    }

    public static EnvEnum of(Integer value) {
        EnvEnum[] envEnums = EnvEnum.values();
        for (EnvEnum envEnum : envEnums) {
            if (envEnum.value.equals(value)) {
                return envEnum;
            }
        }
        return null;
    }
}
