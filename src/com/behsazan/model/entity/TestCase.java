package com.behsazan.model.entity;

import com.behsazan.model.sqlite.SqliteHelper;

import java.util.List;

/**
 * Created by admin on 08/07/2017.
 */
public class TestCase {
    private String name;
    private List<TestCase_Sequence> reqs;
    private int Id;

    public TestCase(String name, List<TestCase_Sequence> reqs) {
        this.name = name;
        this.reqs = reqs;
        for (TestCase_Sequence tcs : reqs) {
            tcs.setTestCase(this);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public static TestCase getById(int id) {
        return new SqliteHelper().getTestCaseById(id);
    }
}
