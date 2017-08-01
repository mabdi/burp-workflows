package com.behsazan.model.entity;

import com.behsazan.model.adapters.RequestListModelObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 07/31/2017.
 */
public class Sequence {
    private int id;
    private List<Request> request;
    private String name;
    private List<RequestListModelObject> requestModels;

    public Sequence(String sequenceName, List<RequestListModelObject> selectedRequests) {
        this.name = sequenceName;
        this.requestModels = selectedRequests;
        parseRequestModels(requestModels);
    }

    public Sequence(int id) {

    }

    private void parseRequestModels(List<RequestListModelObject> requestModels) {
        this.request = new ArrayList<>();
        int rid = 0;
        for (RequestListModelObject rq:requestModels) {
            request.add(new Request(rq.getAnalysed().getUrl(),rq.getRequest(),rq.getResponse(),this,rid));
            id++;
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
}
