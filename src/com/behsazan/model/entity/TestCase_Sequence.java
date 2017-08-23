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
    private TestCase testCase;

    public TestCase_Sequence() {
    }

    public TestCase_Sequence(int id, Sequence sequence , URL url, String base1, String base2, String cookie, List<TestCase_Request> requests ) {
        this.Id = id;
        this.sequence = sequence;
        this.url = url;
        this.base1 = base1;
        this.base2 = base2;
        this.cookie = cookie;
        this.requests = requests;
        for (TestCase_Request ri : requests ) {
            ri.setTestCaseSequence(this);
        }
    }

    public static TestCase_Sequence initBySequence(Sequence sequence) {


        List<TestCase_Request> requests = new ArrayList<>();
        for (Request rq : sequence.getRequest()) {
            requests.add(TestCase_Request.getInstaceFromRequest(rq));
        }
        Request req1 = sequence.getRequest().get(0);
        URL url = null;
        try {
            url = new URL(DataUtils.getRootAddress(req1));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        TestCase_Sequence tsq = new TestCase_Sequence(-1,sequence,url,DataUtils.getBasePath(req1),DataUtils.getBasePath(req1),
                DataUtils.getCookie(req1),requests);
        return tsq;
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

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }
}
