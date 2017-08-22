package com.behsazan.model.entity;

import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.sqlite.SqliteHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by admin on 07/31/2017.
 */
public class Sequence {
    private int id;
    private List<Request> request;
    private String name;
    private String description;

    public Sequence(String sequenceName,String description, List<Request> selectedRequests) {
        this.name = sequenceName;
        this.description = description;
        this.request = selectedRequests;
        for (Request r : request) {
            r.setSequence(this);
        }
    }

    @Override
    public String toString() {
        return id + ": " + name;
    }

    public static Sequence getById(int id) {
        try {
            return new SqliteHelper().getSequenceById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return  null;
        }
    }

    public String getName() {
        return name;
    }

    public List<Request> getRequest() {
        return request;
    }

    public void setRequest(List<Request> request) {
        this.request = request;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public static boolean isSequenceNameUsed(String name) {
        try {
            return new SqliteHelper().isSequenceNameUsed(name);
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static List<Sequence> getAllSequences() {
        try {
            return new SqliteHelper().getAllSequences();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void cloneSequence(int id, String response) throws SQLException {
        new SqliteHelper().cloneSequence(id,response);
    }

    public static boolean isPossibleToDeleteSequence(int id) {
        try {
            return new SqliteHelper().isPossibleToDeleteSequence(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void deleteSequence(int id) throws SQLException {
        new SqliteHelper().deleteSequence(id);
    }

    public static void updateSequenceName(int id, String name) throws SQLException {
        new SqliteHelper().updateSequenceName(id,name);
    }

    public static Vector<Vector<Object>> getAllSequences_Table() {
        try {
            return new SqliteHelper().getAllSequences_Table();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void insertSequence(Sequence sequence) throws SQLException {
        new SqliteHelper().insertSequence(sequence);
    }
}
