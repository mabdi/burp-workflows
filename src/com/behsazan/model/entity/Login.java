package com.behsazan.model.entity;

import com.behsazan.model.sqlite.SqliteHelper;

import java.sql.SQLException;
import java.util.Vector;

/**
 * Created by admin on 08/21/2017.
 */
public class Login {
    private int id;
    private String username;
    private String password;
    private String url;
    private String base;
    private String outParam;
    private String session;
    private int last_seen;
    private TestCase testCase;
    private int testcaseId;

    public Login() {
    }

    public Login(int id, String username, String password, String outParam, String url, String base, String session, int last_seen, TestCase testcase) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.url = url;
        this.base = base;
        this.outParam = outParam;
        this.session = session;
        this.last_seen = last_seen;
        this.testCase = testcase;
        this.testcaseId = testcase.getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public int getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(int last_seen) {
        this.last_seen = last_seen;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }

    public int getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(int testcaseId) {
        this.testcaseId = testcaseId;
    }

    public String getOutParam() {
        return outParam;
    }

    public void setOutParam(String outParam) {
        this.outParam = outParam;
    }

    public static Login getById(int id) {
        try {
            return new SqliteHelper().getLoginById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateLogin(Login login) {
        try {
            new SqliteHelper().updateLogin(login);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void cloneLogin(int id) throws SQLException {
        new SqliteHelper().cloneLogin(id);
    }

    public static Vector<Vector<Object>> getAllLogins_Table() {
        try {
            return new SqliteHelper().getAllLogins_Table();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void insertLogin(Login login) throws SQLException {
        new SqliteHelper().insertLogin(login);
    }

    public static void deleteLogin(int id) throws SQLException {
        new SqliteHelper().deleteLogin(id);
    }
}
