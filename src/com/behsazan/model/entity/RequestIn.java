package com.behsazan.model.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 08/03/2017.
 */
public class RequestIn {
    public static final int TYPE_FROM_LIST = 100;
    public static final int TYPE_LOCAL = 103;
    public static final int TYPE_GLOBAL = 104;
    public static final int TYPE_URL = 106;
    public static final int TYPE_SIGN = 107;
    private static final Map<Integer,String> TYPES_STRING;

    static {
        TYPES_STRING = new HashMap<>();
        TYPES_STRING.put(TYPE_FROM_LIST,"String List");
        TYPES_STRING.put(TYPE_LOCAL,"From Local Variables");
        TYPES_STRING.put(TYPE_GLOBAL,"From Global Variables");
    }

    public static Map<Integer, String> getTypesString() {
        return TYPES_STRING;
    }

    private int id;
    private int type;
    private String placeHoder;
    private String txtValue;
    private TestCase_Request testCase_request;

    public RequestIn(int id, int type, String placeHoder, String txtValue, TestCase_Request testCase_request) {
        this.id = id;
        this.type = type;
        this.placeHoder = placeHoder;
        this.txtValue = txtValue;
        this.testCase_request = testCase_request;
    }

    public int getId() {
        return id;
    }

    public String getPlaceHoder() {
        return placeHoder;
    }

    public String getTypeString() {
        return TYPES_STRING.get(type);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setPlaceHoder(String placeHoder) {
        this.placeHoder = placeHoder;
    }

    public String getTxtValue() {
        return txtValue;
    }

    public void setTxtValue(String txtValue) {
        this.txtValue = txtValue;
    }

    public TestCase_Request getTestCase_request() {
        return testCase_request;
    }

    public void setTestCase_request(TestCase_Request testCase_request) {
        this.testCase_request = testCase_request;
    }
}
