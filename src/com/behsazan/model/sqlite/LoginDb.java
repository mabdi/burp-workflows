package com.behsazan.model.sqlite;

import com.behsazan.model.entity.Flow;
import com.behsazan.model.entity.Login;

import java.sql.*;
import java.util.Vector;

/**
 * Created by admin on 08/24/2017.
 */
public class LoginDb extends SqliteHelper {



    public void insertLogin(Login login) throws SQLException {
        Connection c = getConnection();
        PreparedStatement stmt = c.prepareStatement("INSERT INTO LOGIN " +
                "(USER_NAME,PASSWORD,URL,BASE,FLOW_ID,OUT_PARAM_NAME,SESSION_VALUE,SESSION_CREATE_TIME) VALUES (?,?,?,?,?,?,'',0)");
        stmt.setString(1, login.getUsername());
        stmt.setString(2, login.getPassword());
        stmt.setString(3, login.getUrl());
        stmt.setString(4, login.getBase());
        stmt.setInt(5, login.getFlowId());
        stmt.setString(6, login.getOutParam());
        stmt.executeUpdate();

        stmt.close();
        c.close();
    }


    public Login getLoginById(int id) throws SQLException {
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            c = getConnection();
            stmt = c.prepareStatement("SELECT ID,USER_NAME,PASSWORD,URL,BASE,FLOW_ID,OUT_PARAM_NAME," +
                    "SESSION_VALUE,SESSION_CREATE_TIME from LOGIN WHERE ID= ?");
            stmt.setInt(1, id);
            rq = stmt.executeQuery();
            if (rq.next()) {
                String username = rq.getString(2);
                String password = rq.getString(3);
                String url = rq.getString(4);
                String base = rq.getString(5);
                int flowId = rq.getInt(6);
                String outParam = rq.getString(7);
                String session = rq.getString(8);
                int last_seen = rq.getInt(9);
                Flow flow = Flow.getById(flowId);
                Login r = new Login(id, username, password, outParam, url, base, session, last_seen, flow);
                return r;
            } else {
                return null;
            }
        } finally {
            if (rq != null)
                rq.close();
            if (stmt != null)
                stmt.close();
            if (c != null)
                c.close();
        }
    }


    public void cloneLogin(int id) throws SQLException {
        Login log = getLoginById(id);
        insertLogin(log);

    }


    public void deleteLogin(int id) throws SQLException {
        Connection c = null;
        c = getConnection();

        PreparedStatement stmt = c.prepareStatement("DELETE from LOGIN WHERE ID =?");
        stmt.setInt(1, id);
        stmt.executeUpdate();

        c.close();

    }

    public Vector<Vector<Object>> getAllLogins_Table() throws SQLException {
        Connection c = getConnection();
        Statement stmt = c.createStatement();

        ResultSet allRequests = stmt.executeQuery("SELECT LOGIN.ID,LOGIN.USER_NAME,LOGIN.PASSWORD,LOGIN.OUT_PARAM_NAME,FLOW.NAME,LOGIN.SESSION_CREATE_TIME from LOGIN" +
                " INNER JOIN FLOW ON FLOW.ID = LOGIN.FLOW_ID");
        Vector<Vector<Object>> data = resultSetToVector(allRequests);
        allRequests.close();
        stmt.close();
        c.close();
        return data;
    }

    public void updateLogin(Login login) throws SQLException {

        Connection c = getConnection();
        PreparedStatement stmt = c.prepareStatement("UPDATE LOGIN SET USER_NAME = ?,PASSWORD=?,URL=?,BASE=?, " +
                "FLOW_ID=?,OUT_PARAM_NAME=?,SESSION_VALUE=?,SESSION_CREATE_TIME=? WHERE ID =?");
        stmt.setString(1, login.getUsername());
        stmt.setString(2, login.getPassword());
        stmt.setString(3, login.getUrl());
        stmt.setString(4, login.getBase());
        stmt.setInt(5, login.getFlowId());
        stmt.setString(6, login.getOutParam());
        stmt.setString(7, login.getSession());
        stmt.setInt(8, login.getLast_seen());
        stmt.setInt(9, login.getId());
        stmt.executeUpdate();
        stmt.close();
        c.close();
    }
}
