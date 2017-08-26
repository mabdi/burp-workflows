package com.behsazan.model.sqlite;

import com.behsazan.model.DataUtils;
import com.behsazan.model.entity.Request;
import com.behsazan.model.entity.Sequence;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by admin on 08/24/2017.
 */
public class SequenceDb extends SqliteHelper {

    public void insertSequence(Sequence sq) throws SQLException {
        Connection c = getConnection();
        PreparedStatement stmt = c.prepareStatement("INSERT INTO SEQUENCE (NAME,BASE,DESCRIPTION,REQUEST_COUNT,FIRST_URL,LAST_URL) VALUES (?,?,?,?,?,?)");
        stmt.setString(1, sq.getName());
        stmt.setString(2, sq.getUrl());
        stmt.setString(3, sq.getDescription());
        stmt.setInt(4, sq.getRequest().size());
        stmt.setString(5, sq.getRequest().get(0).getHttpService().toString());
        stmt.setString(6, sq.getRequest().get(sq.getRequest().size() - 1).getHttpService().toString());
        stmt.executeUpdate();

        ResultSet rs = c.createStatement().executeQuery("select last_insert_rowid();");
        rs.next();
        int newid = rs.getInt(1);
        sq.setId(newid);
        int order = 0;
        for (Request rq : sq.getRequest()) {
            PreparedStatement stmt2 = c.prepareStatement("INSERT INTO REQUEST (SID,REQUEST,RESPONSE,URL,ORDER_NUM) VALUES (?,?,?,?,?)");
            stmt2.setInt(1, rq.getSequence().getId());
            stmt2.setBytes(2, rq.getRequest());
            stmt2.setBytes(3, rq.getResponse());
            stmt2.setString(4, rq.getHttpService().toString());
            stmt2.setInt(5, order);
            stmt2.executeUpdate();
            stmt2.close();
            order++;
        }
        stmt.close();
        c.close();
    }


    public Vector<Vector<Object>> getAllSequences_Table() throws SQLException {
        Connection c = getConnection();
        Statement stmt = c.createStatement();
        ResultSet allRequests = stmt.executeQuery("SELECT ID as Id,NAME as Name,REQUEST_COUNT as 'Number Of Requests'," +
                " FIRST_URL as 'First Url',LAST_URL as 'Last Url' from SEQUENCE");
        Vector<Vector<Object>> data = resultSetToVector(allRequests);
        allRequests.close();
        stmt.close();
        c.close();
        return data;
    }


    public boolean isSequenceNameUsed(String name) throws SQLException {
        Connection c = null;
        c = getConnection();
        PreparedStatement stmt = c.prepareStatement("SELECT COUNT(*) from SEQUENCE WHERE NAME =?");
        stmt.setString(1, name);
        ResultSet allRequests = stmt.executeQuery();
        allRequests.next();
        boolean res = allRequests.getInt(1) > 0;
        allRequests.close();
        stmt.close();
        c.close();
        return res;
    }

    public void deleteSequence(int id) throws SQLException {
        Connection c = getConnection();
        PreparedStatement stmt = c.prepareStatement("DELETE from SEQUENCE WHERE ID =?");
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt = c.prepareStatement("DELETE from REQUEST WHERE SID =?");
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt.close();
        c.close();
    }


    public boolean isPossibleToDeleteSequence(int id) throws SQLException {
        Connection c = null;
        c = getConnection();
        PreparedStatement stmt = c.prepareStatement("SELECT COUNT(*) from FLOW_SEQUENCE WHERE SEQUENCE_ID =?");
        stmt.setInt(1, id);
        ResultSet allRequests = stmt.executeQuery();
        allRequests.next();
        boolean res = allRequests.getInt(1) == 0;
        allRequests.close();
        stmt.close();
        c.close();
        return res;
    }


    public void updateSequence(int id, String name, String description, String url) throws SQLException {

        Connection c = getConnection();
        PreparedStatement stmt = c.prepareStatement("UPDATE SEQUENCE SET NAME = ?,DESCRIPTION=?,BASE=? WHERE ID =?");
        stmt.setString(1, name);
        stmt.setString(2, description);
        stmt.setString(3, url);

        stmt.setInt(4, id);
        stmt.executeUpdate();
        stmt.close();
        c.close();

    }


    public List<Sequence> getAllSequences() throws SQLException {
        List<Sequence> res = new ArrayList<>();
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            c = getConnection();
            stmt = c.prepareStatement("SELECT ID,NAME,BASE,REQUEST_COUNT,FIRST_URL,LAST_URL,DESCRIPTION from SEQUENCE");
            rq = stmt.executeQuery();
            while (rq.next()) {
                int id = rq.getInt(1);
                String name = rq.getString(2);
                String url = rq.getString(3);
                String descr = rq.getString(7);
                List<Request> reqs = getSequenceRequestById(id);
                Sequence s = new Sequence(name,  descr,url, reqs);
                s.setId(id);
                res.add(s);
            }
        } finally {
            if (rq != null)
                rq.close();
            if (stmt != null)
                stmt.close();
            if (c != null)
                c.close();
        }
        return res;
    }


    private List<Request> getSequenceRequestById(int requestId) throws SQLException {
        List<Request> res = new ArrayList<>();
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            c = getConnection();
            stmt = c.prepareStatement("SELECT ID,SID,REQUEST,RESPONSE,URL,ORDER_NUM from REQUEST WHERE SID= ?");
            stmt.setInt(1, requestId);
            rq = stmt.executeQuery();
            while (rq.next()) {
                String url = rq.getString(5);
                byte[] request = rq.getBytes(3);
                byte[] response = rq.getBytes(4);
                int order = rq.getInt(6);
                Request r = new Request(DataUtils.makeHttpService(url), request, response, order);
                r.setId(rq.getInt(1));
                res.add(r);
            }
        } finally {
            if (rq != null)
                rq.close();
            if (stmt != null)
                stmt.close();
            if (c != null)
                c.close();
        }
        return res;
    }


    public void cloneSequence(int id, String response) throws SQLException {
        Sequence seq = getSequenceById(id);
        seq.setName(response);
        insertSequence(seq);

    }


    public Sequence getSequenceById(int id) throws SQLException {
        Sequence res = null;
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;

        try {
            c = getConnection();
            stmt = c.prepareStatement("SELECT ID,NAME,REQUEST_COUNT,FIRST_URL,LAST_URL,DESCRIPTION,BASE from SEQUENCE WHERE ID=?");
            stmt.setInt(1, id);
            rq = stmt.executeQuery();
            if (!rq.next()) {
                res = null;
            } else {
                String name = rq.getString(2);
                String url = rq.getString(7);
                String desc = rq.getString(6);
                List<Request > reqs = getSequenceRequestById(id);
                Sequence s = new Sequence(name, desc, url, reqs);
                s.setId(id);
                res = s;
            }
        } finally {
            if (rq != null)
                rq.close();
            if (stmt != null)
                stmt.close();
            if (c != null)
                c.close();
        }

        return res;
    }


}
