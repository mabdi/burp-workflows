package com.behsazan.controller;

import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.Flow;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 08/15/2017.
 */
public class Flow_Running {
    private static Map<String,String> GLOBALS;
    private Map<String,String> locals;
    private Map<String,String> params;

    private static DefaultTableModel modelGlobal;
    private Flow flow;
    private String baseUrl;
    private int order;

    static {
        GLOBALS = new HashMap<>();
    }

    private List<RequestListModelObject> requestModelItem;
    private DefaultTableModel modelLocal;

    public Flow_Running() {
    }

    public Flow_Running(Flow flow, String baseUrl, Map<String, String> params, int order) {
        this.baseUrl = baseUrl;
        this.flow = flow;
        this.locals = new HashMap<>();
        this.params = params;
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

    public Flow getFlow() {
        return flow;
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

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public static void setGLOBALS(Map<String, String> GLOBALS) {
        Flow_Running.GLOBALS = GLOBALS;
    }

    public void setLocals(Map<String, String> locals) {
        this.locals = locals;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public static Map<String, String> getGLOBALS() {
        return GLOBALS;
    }

    public Map<String, String> getLocals() {
        return locals;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
