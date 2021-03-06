package com.behsazan.controller;

import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.Flow;
import com.behsazan.model.sqlite.GlobalsDb;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
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
    private String name;
    private String baseUrl;
    private int order;

    private List<RequestListModelObject> requestModelItem;
    private DefaultTableModel modelLocal;
//    private DefaultListModel<RequestListModelObject> modelRequests;


    static {
        try {
            GLOBALS = new GlobalsDb().loadGlobals();
        } catch (SQLException e) {
            e.printStackTrace();
            GLOBALS = new HashMap<>();
        }
    }

    public Flow_Running() {
    }

    public String getName() {
        return name;
    }

    public List<RequestListModelObject> getRequestModelItem() {
        return requestModelItem;
    }

    public Flow_Running(Flow flow, String name, String baseUrl, Map<String, String> params, int order) {
        this.baseUrl = baseUrl;
        this.flow = flow;
        this.name = name;
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
        try {
            new GlobalsDb().updateKey(key,value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        globalsToTableModel();
    }

    public static String queryGlobalVariable(String key){
        return GLOBALS.get(key);
    }


    public void addItemToRequests(RequestListModelObject obj) {
        requestModelItem.add(obj);
    }

//    public void addItemToRequestsModel(RequestListModelObject obj){
//        getRequestsListModel().addElement(obj);
//    }

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

//    public DefaultListModel<RequestListModelObject> getRequestsListModel() {
//        if(modelRequests == null){
//            modelRequests = new DefaultListModel<>();
//        }
//        return modelRequests;
//    }
}
