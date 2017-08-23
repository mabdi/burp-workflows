package com.behsazan.model.entity;

import com.behsazan.model.adapters.RequestListModelObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 08/15/2017.
 */
public class TestCaseInstance {
    private static Map<String,String> GLOBALS;
    private static DefaultTableModel modelGlobal;
    private TestCase testCase;
    private Map<Integer, String> initParams; // RequestIn -> String (variable name or constant)
    private Map<String,String> locals;
    private int order;

    static {
        GLOBALS = new HashMap<>();
    }

    private List<RequestListModelObject> requestModelItem;
    private DefaultTableModel modelLocal;

    public TestCaseInstance() {
    }

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
        localsToTableModel();
    }

    public String queryLocalVariable(String key){
        return locals.get(key);
    }

    public static void updateGlobalVariable(String key, String value){
        GLOBALS.put(key,value);
        globalsToTableModel();
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

    public static DefaultTableModel globalsToTableModel() {
        if(modelGlobal==null) {
            modelGlobal = new DefaultTableModel(
                    new Object[]{"Key", "Value"}, 0
            );
        }
        modelGlobal.getDataVector().removeAllElements();
        for (Map.Entry<?,?> entry : GLOBALS.entrySet()) {
            modelGlobal.addRow(new Object[] { entry.getKey(), entry.getValue() });
        }
        modelGlobal.fireTableDataChanged();
        return modelGlobal;
    }

    public DefaultTableModel localsToTableModel() {
        if(modelLocal == null) {
            modelLocal = new DefaultTableModel(
                    new Object[]{"Key", "Value"}, 0
            );
        }
        modelLocal.getDataVector().removeAllElements();
        for (Map.Entry<?,?> entry : locals.entrySet()) {
            modelLocal.addRow(new Object[] { entry.getKey(), entry.getValue() });
        }
        modelLocal.fireTableDataChanged();
        return modelLocal;
    }
}
