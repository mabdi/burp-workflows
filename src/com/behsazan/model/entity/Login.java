package com.behsazan.model.entity;

import com.behsazan.model.sqlite.LoginDb;

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
    private String outParam;
    private String session;
    private int last_seen;
    private Flow flow;
    private int flowId;

    public Login() {
    }

    public Login(int id, String username, String password, String outParam, String url, String session, int last_seen, Flow flow) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.url = url;
        this.outParam = outParam;
        this.session = session;
        this.last_seen = last_seen;
        this.flow = flow;
        this.flowId = flow.getId();
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

    public Flow getFlow() {
        return flow;
    }

    public void setFlow(Flow flow) {
        this.flow = flow;
    }

    public int getFlowId() {
        return flowId;
    }

    public void setFlowId(int flowId) {
        this.flowId = flowId;
    }

    public String getOutParam() {
        return outParam;
    }

    public void setOutParam(String outParam) {
        this.outParam = outParam;
    }

    public static Login getById(int id) {
        try {
            return new LoginDb().getLoginById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateLogin(Login login) {
        try {
            new LoginDb().updateLogin(login);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void cloneLogin(int id) throws SQLException {
        new LoginDb().cloneLogin(id);
    }

    public static Vector<Vector<Object>> getAllLogins_Table() {
        try {
            return new LoginDb().getAllLogins_Table();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void insertLogin(Login login) throws SQLException {
        new LoginDb().insertLogin(login);
    }

    public static void deleteLogin(int id) throws SQLException {
        new LoginDb().deleteLogin(id);
    }
}
