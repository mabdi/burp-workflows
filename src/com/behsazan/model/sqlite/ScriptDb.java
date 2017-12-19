package com.behsazan.model.sqlite;

import com.behsazan.model.entity.Script;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by admin on 08/24/2017.
 */
public class ScriptDb extends SqliteHelper {

    public void insertScript(Script sq) throws SQLException {
        Connection c = getConnection();
        PreparedStatement stmt = c.prepareStatement("INSERT INTO SCRIPT (NAME,BODY,TYPE,LANG) VALUES (?,?,?,?)");
        stmt.setString(1, sq.getName());
        stmt.setString(2, sq.getText());
        stmt.setInt(3, sq.getType());
        stmt.setInt(4, sq.getLang());
        stmt.executeUpdate();
        stmt.close();
        c.close();
    }


    public Vector<Vector<Object>> getAllScripts_Table() throws SQLException {
        Connection c = getConnection();
        Statement stmt = c.createStatement();
        ResultSet allRequests = stmt.executeQuery("SELECT ID,NAME, TYPE ,LANG from SCRIPT");
        Vector<Vector<Object>> data = resultSetToVector(allRequests);
        allRequests.close();
        stmt.close();
        c.close();
        return data;
    }


    public boolean isScriptNameUsed(String name) throws SQLException {
        Connection c = null;
        c = getConnection();
        PreparedStatement stmt = c.prepareStatement("SELECT COUNT(*) from SCRIPT WHERE NAME =?");
        stmt.setString(1, name);
        ResultSet allRequests = stmt.executeQuery();
        allRequests.next();
        boolean res = allRequests.getInt(1) > 0;
        allRequests.close();
        stmt.close();
        c.close();
        return res;
    }

    public void deleteScript(int id) throws SQLException {
        Connection c = getConnection();
        PreparedStatement stmt = c.prepareStatement("DELETE from SCRIPT WHERE ID =?");
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt.close();
        c.close();
    }


    public boolean isPossibleToDeleteScript(int id) throws SQLException {
        Connection c = null;
        c = getConnection();
        PreparedStatement stmt = c.prepareStatement("SELECT COUNT(*) from FLOW_SCRIPT WHERE SCRIPT_ID =?");
        stmt.setInt(1, id);
        ResultSet allRequests = stmt.executeQuery();
        allRequests.next();
        boolean res = allRequests.getInt(1) == 0;
        allRequests.close();
        stmt.close();
        c.close();
        return res;
    }


    public void updateScript(Script script) throws SQLException {

        Connection c = getConnection();
        PreparedStatement stmt = c.prepareStatement("UPDATE SCRIPT SET NAME = ?,BODY=?,TYPE =?,LANG=? WHERE ID =?");
        stmt.setString(1, script.getName());
        stmt.setString(2, script.getText());
        stmt.setInt(3, script.getType());
        stmt.setInt(4, script.getLang());

        stmt.setInt(5, script.getId());
        stmt.executeUpdate();
        stmt.close();
        c.close();

    }


    public List<Script> getAllScripts() throws SQLException {
        List<Script> res = new ArrayList<>();
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            c = getConnection();
            stmt = c.prepareStatement("SELECT ID,NAME,BODY,TYPE,LANG from SCRIPT");
            rq = stmt.executeQuery();
            while (rq.next()) {
                int id = rq.getInt(1);
                String name = rq.getString(2);
                String text = rq.getString(3);
                int type = rq.getInt(4);
                int lang = rq.getInt(5);
                Script s = new Script(id,name, text,type,lang);
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


    public void cloneScript(int id, String newName) throws SQLException {
        Script seq = getScriptById(id);
        seq.setName(newName);
        insertScript(seq);
    }


    public Script getScriptById(int sid) throws SQLException {
        Script res = null;
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;

        try {
            c = getConnection();
            stmt = c.prepareStatement("SELECT ID,NAME,BODY,TYPE,LANG from SCRIPT WHERE ID=?");
            stmt.setInt(1, sid);
            rq = stmt.executeQuery();
            if (!rq.next()) {
                res = null;
            } else {
                int id = rq.getInt(1);
                String name = rq.getString(2);
                String text = rq.getString(3);
                int type = rq.getInt(4);
                int lang = rq.getInt(5);
                Script s = new Script(id,name, text,type,lang);
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
