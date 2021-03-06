package com.behsazan.model.entity;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 08/03/2017.
 */
public class ResponseOut {
    public static final int TYPE_HIDDEN = 102;
    public static final int TYPE_COOKIE = 103;
    public static final int TYPE_CAPTCHA = 105;
    public static final int TYPE_CSS = 106;
    public static final int TYPE_REGEX = 107;

    @Expose
    private int id;
    @Expose
    private String name;
    @Expose
    private String param;
    @Expose
    private int type;
    @Expose
    private boolean global;

    private Flow_Request flow_request;
    private static Map<Integer, String> TYPES_STRING;

    static {
        TYPES_STRING = new HashMap<>();
        TYPES_STRING.put(TYPE_HIDDEN,"Form Hidden Field");
        TYPES_STRING.put(TYPE_COOKIE,"Response Cookie");
        TYPES_STRING.put(TYPE_CAPTCHA,"Captcha");
        TYPES_STRING.put(TYPE_CSS,"CSS Selector");
        TYPES_STRING.put(TYPE_REGEX,"Regex");
    }

    public ResponseOut() {
    }

    public static Map<Integer, String> getTypesString() {
        return TYPES_STRING;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTypeString() {
        return TYPES_STRING.get(type);
    }
    public ResponseOut(int id,int type, String name, String param, boolean global) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.param = param;
        this.global = global;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public Flow_Request getFlow_request() {
        return flow_request;
    }

    public void setFlow_request(Flow_Request flow_request) {
        this.flow_request = flow_request;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
