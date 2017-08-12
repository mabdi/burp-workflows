package com.behsazan.model.entity;

import java.util.List;

/**
 * Created by admin on 08/07/2017.
 */
public class TestCase {
    private String name;
    private String base1;
    private String base2;
    private String url;
    private String cookie;
    private List<TestCase_Sequence> reqs;
    private int Id;

    public TestCase(String name, String base1, String base2, String url, String cookie, List<TestCase_Sequence> reqs) {
        this.name = name;
        this.base1 = base1;
        this.base2 = base2;
        this.url = url;
        this.cookie = cookie;
        this.reqs = reqs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBase1() {
        return base1;
    }

    public void setBase1(String base1) {
        this.base1 = base1;
    }

    public String getBase2() {
        return base2;
    }

    public void setBase2(String base2) {
        this.base2 = base2;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public List<TestCase_Sequence> getReqs() {
        return reqs;
    }

    public void setReqs(List<TestCase_Sequence> reqs) {
        this.reqs = reqs;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
}
