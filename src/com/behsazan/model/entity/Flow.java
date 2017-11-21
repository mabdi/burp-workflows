package com.behsazan.model.entity;

import com.behsazan.model.DataUtils;
import com.behsazan.model.sqlite.FlowDb;
import com.google.gson.annotations.Expose;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

/**
 * Created by admin on 08/07/2017.
 */
public class Flow {
    @Expose
    private String name;
    @Expose
    private List<Flow_Sequence> seqs;
    @Expose
    private int Id;
    @Expose
    private String description;
    @Expose
    private String parameters;
    @Expose
    private List<Script> scripts;


    public Flow() {
    }

    public Flow(String name, String description, String parameters, List<Flow_Sequence> seqs,List<Script> scripts) {
        this.name = name;
        this.seqs = seqs;
        this.scripts = scripts;
        this.parameters = parameters;
        this.description = description;
        for (Flow_Sequence tcs : seqs) {
            tcs.setFlow(this);
        }
    }

    public List<Script> getScripts() {
        return scripts;
    }

    public void setScripts(List<Script> scripts) {
        this.scripts = scripts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<Flow_Sequence> getSeqs() {
        return seqs;
    }

    public void setSeqs(List<Flow_Sequence> seqs) {
        this.seqs = seqs;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public static Flow getById(int id) {
        try {
            return new FlowDb().getFlowById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static Flow getByName(String selectedFlow) {
        try {
            return new FlowDb().getFlowByName(selectedFlow);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> getAllFlowName() {
        try {
            return new FlowDb().getAllFlowName();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isFlowNameUsed(String name) {
        try {
            return new FlowDb().isFlowNameUsed(name);
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static void cloneFlow(int id, String response) throws SQLException {
        new FlowDb().cloneFlow(id, response);
    }

    public static Vector<Vector<Object>> getAllFlows_Table() {
        try {
            return new FlowDb().getAllFlows_Table();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteFlow(int id) throws SQLException {
        new FlowDb().deleteFlow(id);
    }

    public static void insertFlow(Flow flow) throws SQLException {
        new FlowDb().insertFlow(flow);
    }

    public static void updateFlow(Flow flow) throws SQLException {
        new FlowDb().updateFlow(flow);
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String[] getParametersExploded() {
        String[] list = getParameters().split(",");
        for (int i = 0; i < list.length; i++) {
            list[i] = list[i].trim();
        }
        return list;
    }

    public static List<Flow> getAllFlows() {
        try {
            return new FlowDb().getAllFlows();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
