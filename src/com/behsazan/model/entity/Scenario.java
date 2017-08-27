package com.behsazan.model.entity;

import com.behsazan.model.sqlite.ScenarioDb;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by admin on 08/21/2017.
 */
public class Scenario {
    private int id;
    private String name;
    private String description;
    private String params_json;
    private Map<String,String[]> params_map;
    private String url;
    private String outParam;
    private Flow flow;
    private int flowId;

    public Scenario() {
    }

    public Scenario(int id, String name, String description, String params_json, String outParam, String url, Flow flow) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.description = description;
        setParams_json(params_json);
        this.outParam = outParam;
        this.flow = flow;
        this.flowId = flow.getId();
    }

    public Scenario(int id, String name, String description, Map<String,String[]> params_map, String outParam, String url, Flow flow) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.description = description;
        setParams_map(params_map);
        this.outParam = outParam;
        this.flow = flow;
        this.flowId = flow.getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Flow getFlow() {
        return flow;
    }

    public void setFlow(Flow flow) {
        this.flow = flow;
    }

    public int getFlowId() {
        return flowId;
    }

    public void setFlowId(int flowId) {
        this.flowId = flowId;
    }

    public String getOutParam() {
        return outParam;
    }

    public void setOutParam(String outParam) {
        this.outParam = outParam;
    }

    public String getParams_json() {
        return params_json;
    }

    public void setParams_json(String params_json) {
        this.params_json = params_json;
        this.params_map = toMapParam(params_json);
    }

    public Map<String, String[]> getParams_map() {
        return params_map;
    }

    public void setParams_map(Map<String, String[]> params_map) {
        this.params_map = params_map;
        this.params_json = toJsonParam(params_map);
    }

    public static Scenario getById(int id) {
        try {
            return new ScenarioDb().getScenarioById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateScenario(Scenario scenario) {
        try {
            new ScenarioDb().updateScenario(scenario);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void cloneScenario(int id) throws SQLException {
        new ScenarioDb().cloneScenario(id);
    }

    public static Vector<Vector<Object>> getAllSenarios_Table() {
        try {
            return new ScenarioDb().getAllScenarios_Table();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void insertScenario(Scenario scenario) throws SQLException {
        new ScenarioDb().insertScenario(scenario);
    }

    public static void deleteScenario(int id) throws SQLException {
        new ScenarioDb().deleteScenario(id);
    }

    public static String toJsonParam(Map<String,String[]> map){
        Gson gson = new GsonBuilder().create();
        return gson.toJson(map);
    }

    public static Map<String,String[]> toMapParam(String json){
        Gson gson = new GsonBuilder().create();
        Map<String,String[]> map = new HashMap<>();
        return (Map<String,String[]>)gson.fromJson(json, map.getClass());
    }
}
