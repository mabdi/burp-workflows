package com.behsazan.model.entity;

import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.sqlite.SqliteHelper;

import java.util.ArrayList;
import java.util.List;

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
        return new SqliteHelper().getSequenceById(id);
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
}
