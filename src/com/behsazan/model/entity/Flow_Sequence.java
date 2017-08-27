package com.behsazan.model.entity;

import com.behsazan.model.DataUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 08/07/2017.
 */
public class Flow_Sequence {
    private Sequence sequence;
    private List<Flow_Request> requests;
//    private URL url;
//    private String base1;
//    private String base2;
    private String cookie;
    private int Id;
    private Flow flow;

    public Flow_Sequence() {
    }

    public Flow_Sequence(int id, Sequence sequence , String cookie, List<Flow_Request> requests ) {
        this.Id = id;
        this.sequence = sequence;
//        this.url = url;
//        this.base1 = base1;
//        this.base2 = base2;
        this.cookie = cookie;
        this.requests = requests;
        for (Flow_Request ri : requests ) {
            ri.setFlowSequence(this);
        }
    }

    public static Flow_Sequence initBySequence(Sequence sequence) {


        List<Flow_Request> requests = new ArrayList<>();
        for (Request rq : sequence.getRequest()) {
            requests.add(Flow_Request.getInstaceFromRequest(rq));
        }
        Request req1 = sequence.getRequest().get(0);
        URL url = null;
        try {
            url = new URL(DataUtils.getRootAddress(req1));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Flow_Sequence tsq = new Flow_Sequence(-1,sequence, DataUtils.getCookie(req1),requests);
        return tsq;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public List<Flow_Request> getRequests() {
        return requests;
    }

//    public URL getUrl() {
//        return url;
//    }
//
//    public void setUrl(URL url) {
//        this.url = url;
//    }
//
//    public String getBase1() {
//        return base1;
//    }
//
//    public void setBase1(String base1) {
//        this.base1 = base1;
//    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

//    public String getBase2() {
//        return base2;
//    }
//
//    public void setBase2(String base2) {
//        this.base2 = base2;
//    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public Flow getFlow() {
        return flow;
    }

    public void setFlow(Flow flow) {
        this.flow = flow;
    }
}
