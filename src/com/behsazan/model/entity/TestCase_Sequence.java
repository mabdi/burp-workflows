package com.behsazan.model.entity;

import com.behsazan.model.DataUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 08/07/2017.
 */
public class TestCase_Sequence {
    private Sequence sequence;
    private List<TestCase_Request> requests;
    private URL url;
    private String base1;
    private String base2;
    private String cookie;
    private int Id;

    public TestCase_Sequence(Sequence sequence) {
        this.sequence = sequence;
        this.requests = new ArrayList<>();
        for (Request rq : sequence.getRequest()) {
            requests.add(new TestCase_Request(rq));
        }
        Request req1 = sequence.getRequest().get(0);
        try {
            setUrl(new URL(DataUtils.getRootAddress(req1)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        setBase1(DataUtils.getBasePath(req1));
        setBase2(DataUtils.getBasePath(req1));
        setCookie(DataUtils.getCookie(req1));
    }

    public Sequence getSequence() {
        return sequence;
    }

    public List<TestCase_Request> getRequests() {
        return requests;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getBase1() {
        return base1;
    }

    public void setBase1(String base1) {
        this.base1 = base1;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getBase2() {
        return base2;
    }

    public void setBase2(String base2) {
        this.base2 = base2;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
}
