package com.behsazan.model.sqlite;

import com.behsazan.model.DataUtils;
import com.behsazan.model.entity.Request;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by admin on 08/24/2017.
 */
public class RequestDb extends SqliteHelper{


    public Request getRequestById(int Id) throws SQLException {
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            c = getConnection();
            stmt = c.prepareStatement("SELECT ID,SID,REQUEST,RESPONSE,URL,ORDER_NUM from REQUEST WHERE ID= ?");
            stmt.setInt(1, Id);
            rq = stmt.executeQuery();
            if (rq.next()) {
                String url = rq.getString(5);
                byte[] request = rq.getBytes(3);
                byte[] response = rq.getBytes(4);
                int order = rq.getInt(6);
                Request  r = new Request (DataUtils.makeHttpService(url), request, response, order);
                r.setId(rq.getInt(1));
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

    public void updateRequestRequest(int id, byte[] message) throws SQLException {

        Connection c = getConnection();
        PreparedStatement stmt = c.prepareStatement("UPDATE REQUEST SET REQUEST = ? WHERE ID =?");
        stmt.setBytes(1, message);
        stmt.setInt(2, id);
        stmt.executeUpdate();
        stmt.close();
        c.close();

    }

    public void updateRequestResponse(int id, byte[] message) throws SQLException {

        Connection c = getConnection();
        PreparedStatement stmt = c.prepareStatement("UPDATE REQUEST SET RESPONSE = ? WHERE ID =?");
        stmt.setBytes(1, message);
        stmt.setInt(2, id);
        stmt.executeUpdate();
        stmt.close();
        c.close();
    }
}
