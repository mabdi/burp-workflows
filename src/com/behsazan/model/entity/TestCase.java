package com.behsazan.model.entity;

import com.behsazan.model.sqlite.SqliteHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 08/07/2017.
 */
public class TestCase {
    private String name;
    private List<TestCase_Sequence> seqs;
    private int Id;
    private String description;

    public TestCase(String name,String description, List<TestCase_Sequence> seqs) {
        this.name = name;
        this.seqs = seqs;
        this.description = description;
        for (TestCase_Sequence tcs : seqs) {
            tcs.setTestCase(this);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<TestCase_Sequence> getSeqs() {
        return seqs;
    }

    public void setSeqs(List<TestCase_Sequence> seqs) {
        this.seqs = seqs;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
