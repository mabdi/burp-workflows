package com.behsazan.model.entity;

import com.behsazan.model.sqlite.SequenceDb;
import com.google.gson.annotations.Expose;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

/**
 * Created by admin on 07/31/2017.
 */
public class Sequence {
    @Expose
    private String url;
    @Expose
    private int id;
    @Expose
    private List<Request> request;
    @Expose
    private String name;
    @Expose
    private String description;

    public Sequence() {
    }

    public Sequence(String sequenceName, String description, String url, List<Request> selectedRequests) {
        this.name = sequenceName;
        this.description = description;
        this.request = selectedRequests;
        this.url = url;
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
            return new SequenceDb().getSequenceById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return  null;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
            return new SequenceDb().isSequenceNameUsed(name);
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static List<Sequence> getAllSequences() {
        try {
            return new SequenceDb().getAllSequences();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void cloneSequence(int id, String response) throws SQLException {
        new SequenceDb().cloneSequence(id,response);
    }

    public static boolean isPossibleToDeleteSequence(int id) {
        try {
            return new SequenceDb().isPossibleToDeleteSequence(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void deleteSequence(int id) throws SQLException {
        new SequenceDb().deleteSequence(id);
    }

    public static void updateSequence(int id, String name, String description, String url) throws SQLException {
        new SequenceDb().updateSequence(id,name,description,url);
    }

    public static Vector<Vector<Object>> getAllSequences_Table() {
        try {
            return new SequenceDb().getAllSequences_Table();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void insertSequence(Sequence sequence) throws SQLException {
        new SequenceDb().insertSequence(sequence);
    }
}
