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

    public Sequence(String sequenceName, List<Request> selectedRequests) {
        this.name = sequenceName;
        this.request = selectedRequests;
        for (Request r : request) {
            r.setSequence(this);
        }
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
}
