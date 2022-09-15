package com.dbquery.common;

import java.io.Serializable;

/**
 * @author yuanguangxin
 */
public class CommonResponse implements Serializable {

    private boolean ok;
    private String message;
    private Object obj;

    public CommonResponse(Object obj) {
        this.ok = true;
        this.message = "ok";
        this.obj = obj;
    }

    public CommonResponse(boolean ok, String message, Object obj) {
        this.ok = ok;
        this.message = message;
        this.obj = obj;
    }

    public static CommonResponse ok() {
        return new CommonResponse(null);
    }

    public static CommonResponse ok(Object obj) {
        return new CommonResponse(obj);
    }

    public static CommonResponse notOk(String message) {
        return new CommonResponse(false, message, null);
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

