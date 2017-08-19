package com.behsazan.model.entity;

import com.behsazan.model.adapters.RequestListModelObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 08/15/2017.
 */
public class TestCaseInstance {
    private static Map<String,String> GLOBALS;
    private final TestCase testCase;
    private Map<Integer, String> initParams; // RequestIn -> String (variable name or constant)
    private Map<String,String> locals;
    private int order;

    static {
        GLOBALS = new HashMap<>();
    }

    private final List<RequestListModelObject> requestModelItem;

    public TestCaseInstance(TestCase testCase, Map<Integer, String> maps, int order) {

        this.testCase = testCase;
        this.locals = new HashMap<>();
        this.initParams = maps;
        this.order = order;
        requestModelItem = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Instance #" + order;
    }

    public void updateLocalVariable(String key, String value){
        locals.put(key,value);
    }

    public String queryLocalVariable(String key){
        return locals.get(key);
    }

    public static void updateGlobalVariable(String key, String value){
        GLOBALS.put(key,value);
    }

    public static String queryGlobalVariable(String key){
        return GLOBALS.get(key);
    }


    public List<RequestListModelObject> getRequestModelItem() {
        return requestModelItem;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public String getInitParamFor(RequestIn rqin){
        String val = initParams.get(rqin.getId());
        switch (rqin.getType()){
            case RequestIn.TYPE_FROM_LIST:
                return val;
            case RequestIn.TYPE_LOCAL:
                return queryLocalVariable(val);
            case RequestIn.TYPE_GLOBAL:
                return queryGlobalVariable(val);
        }
        return val;
    }
}
