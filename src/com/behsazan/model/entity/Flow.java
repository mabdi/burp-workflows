package com.behsazan.model.entity;

import com.behsazan.model.sqlite.FlowDb;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

/**
 * Created by admin on 08/07/2017.
 */
public class Flow {
    private String name;
    private List<Flow_Sequence> seqs;
    private int Id;
    private String description;

    public Flow() {
    }

    public Flow(String name, String description, List<Flow_Sequence> seqs) {
        this.name = name;
        this.seqs = seqs;
        this.description = description;
        for (Flow_Sequence tcs : seqs) {
            tcs.setFlow(this);
        }
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
}
