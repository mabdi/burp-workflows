package com.behsazan.model.entity;

import com.behsazan.model.sqlite.SqliteHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by admin on 08/07/2017.
 */
public class TestCase {
    private String name;
    private List<TestCase_Sequence> seqs;
    private int Id;
    private String description;

    public TestCase() {
    }

    public TestCase(String name, String description, List<TestCase_Sequence> seqs) {
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
        try {
            return new SqliteHelper().getTestCaseById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static TestCase getByName(String selectedTestCase) {
        try {
            return new SqliteHelper().getTestCaseByName(selectedTestCase);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> getAllTestCaseName() {
        try {
            return new SqliteHelper().getAllTestCaseName();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isTestCaseNameUsed(String name) {
        try {
            return new SqliteHelper().isTestCaseNameUsed(name);
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static void cloneTestCase(int id, String response) throws SQLException {
        new SqliteHelper().cloneTestCase(id, response);
    }

    public static Vector<Vector<Object>> getAllTestCases_Table() {
        try {
            return new SqliteHelper().getAllTestCases_Table();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteTestCase(int id) throws SQLException {
        new SqliteHelper().deleteTestCase(id);
    }

    public static void insertTestCase(TestCase testCase) throws SQLException {
        new SqliteHelper().insertTestCase(testCase);
    }

    public static void updateTestCase(TestCase testCase) throws SQLException {
        new SqliteHelper().updateTestCase(testCase);
    }
}
