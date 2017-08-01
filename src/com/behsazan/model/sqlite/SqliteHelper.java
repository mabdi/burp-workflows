package com.behsazan.model.sqlite;

import com.behsazan.model.DataUtils;
import com.behsazan.model.entity.Request;
import com.behsazan.model.entity.Sequence;

import javax.sql.rowset.serial.SerialBlob;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by admin on 07/31/2017.
 */
public class SqliteHelper {
    public static final int DB_VERSION = 1;


    public void initDb(){
        try {
            Connection c = getConnection();
            Statement stmt = c.createStatement();


            ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='APPMETA'");
            if ( ! rs.next() ) {
                stmt.executeUpdate("CREATE TABLE APPMETA " +
                        "(ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " NAME           TEXT    NOT NULL, " +
                        " VALUE        TEXT NOT NULL)");
                stmt.executeUpdate("INSERT INTO APPMETA " +
                        "(NAME,VALUE) VALUES ('DB_VERSION','0')");
            }
            rs.close();

            rs = stmt.executeQuery("SELECT VALUE FROM APPMETA WHERE NAME='DB_VERSION'");
            int ver = 0;
            if(rs.next()){
                ver = Integer.parseInt(rs.getString("VALUE"));
            }
            if(ver!=DB_VERSION){
                update_Db(stmt,ver,DB_VERSION);
            }
            stmt.executeUpdate("UPDATE APPMETA " +
                    "SET VALUE = '"+DB_VERSION+"' WHERE NAME = 'DB_VERSION'");
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    private Connection getConnection() throws  SQLException {
        String appHome = DataUtils.getAppHome();
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection c = DriverManager.getConnection("jdbc:sqlite:"+appHome+"/data.db");

        return c;
    }

    private void update_Db(Statement stmt, int fromVer, int toVer) throws SQLException {
        int ver = fromVer;
        if(ver<1){
            update_v1(stmt);
            ver++;
        }
    }

    private void update_v1(Statement stmt) throws SQLException {

        String createTableSquence = "CREATE TABLE SEQUENCE " +
                "(ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                " NAME           TEXT    NOT NULL, " +
                " REQUEST_COUNT            INTEGER     NOT NULL, " +
                " FIRST_URL        TEXT NOT NULL, " +
                " LAST_URL         TEXT NOT NULL)";
        String createTableRequest = "CREATE TABLE REQUEST " +
                "(ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                " SID           INTEGER    NOT NULL, " +
                " REQUEST         BLOB     NOT NULL, " +
                " RESPONSE        BLOB     NOT NULL, " +
                " URL        TEXT NOT NULL, " +
                " ORDER_NUM      INTEGER NOT NULL " +
                " )";

        stmt.executeUpdate(createTableSquence);
        stmt.executeUpdate(createTableRequest);
        stmt.close();

    }


    public void insertSequence(Sequence sq) throws Exception {
        Connection c = getConnection();
        PreparedStatement  stmt = c.prepareStatement("INSERT INTO SEQUENCE (NAME,REQUEST_COUNT,FIRST_URL,LAST_URL) VALUES (?,?,?,?)");
        stmt.setString(1,sq.getName());
        stmt.setInt(2,sq.getRequest().size());
        stmt.setString(3,sq.getRequest().get(0).getUrl().toString());
        stmt.setString(4,sq.getRequest().get(sq.getRequest().size()-1).getUrl().toString());
        stmt.executeUpdate();

        ResultSet rs = c.createStatement().executeQuery( "select last_insert_rowid();");
        rs.next();
        int newid = rs.getInt(1);
        sq.setId(newid);
        int order = 0;
        for (Request rq :sq.getRequest()) {
            PreparedStatement  stmt2 = c.prepareStatement("INSERT INTO REQUEST (SID,REQUEST,RESPONSE,URL,ORDER_NUM) VALUES (?,?,?,?,?)");
            stmt2.setInt(1,rq.getSequence().getId());
            stmt2.setBytes(2,rq.getRequest());
            stmt2.setBytes(3,rq.getResponse());
            stmt2.setString(4,rq.getUrl().toString());
            stmt2.setInt(5,order);
            stmt2.executeUpdate();
            stmt2.close();
            order++;
        }
        stmt.close();
        c.close();
    }

    public Vector<Vector<Object>> getAllSequences() throws SQLException {
        Connection c = getConnection();
        Statement stmt = c.createStatement();
        ResultSet allRequests = stmt.executeQuery("SELECT ID as Id,NAME as Name,REQUEST_COUNT as 'Number Of Requests',FIRST_URL as 'First Url',LAST_URL as 'Last Url' from SEQUENCE");
        int columnCount = allRequests.getMetaData().getColumnCount();
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (allRequests.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(allRequests.getObject(columnIndex));
            }
            data.add(vector);
        }
        allRequests.close();
        stmt.close();
        c.close();
        return data;
    }

    public boolean isSequenceNameUsed(String name) {
        Connection c = null;
        try {
            c = getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT COUNT(*) from SEQUENCE WHERE NAME =?");
            stmt.setString(1,name);
            ResultSet allRequests = stmt.executeQuery();
            allRequests.next();
            boolean res = allRequests.getInt(1)>0;
            allRequests.close();
            stmt.close();
            c.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteSequence(int id) {
        try {
            Connection c = getConnection();
            PreparedStatement stmt = c.prepareStatement("DELETE from SEQUENCE WHERE ID =?");
            stmt.setInt(1,id);
            stmt.executeUpdate();
            stmt.close();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSequenceName(int id, String name) {
        try {
            Connection c = getConnection();
            PreparedStatement stmt = c.prepareStatement("UPDATE SEQUENCE SET NAME = ? WHERE ID =?");
            stmt.setString(1,name);
            stmt.setInt(2,id);
            stmt.executeUpdate();
            stmt.close();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Sequence getSequenceById(int id) {
        Sequence res = null;
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            try {
                c = getConnection();
                stmt = c.prepareStatement("SELECT ID,NAME,REQUEST_COUNT,FIRST_URL,LAST_URL from SEQUENCE WHERE ID=?");
                stmt.setInt(1, id);
                rq = stmt.executeQuery();
                if (!rq.next()){
                    res = null;
                }else {
                    String name = rq.getString(2);
                    List<Request> reqs = getSequenceRequestById(id);
                    Sequence s = new Sequence(name, reqs);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    private List<Request> getSequenceRequestById(int requestId) {
        List<Request> res = new ArrayList<>();
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            try {
                c = getConnection();
                stmt = c.prepareStatement("SELECT ID,SID,REQUEST,RESPONSE,URL,ORDER_NUM from REQUEST WHERE SID= ?");
                stmt.setInt(1, requestId);
                rq = stmt.executeQuery();
                while (rq.next()){
                    String url = rq.getString(5);
                    byte[] request = rq.getBytes(3);
                    byte[] response = rq.getBytes(4);
                    int order = rq.getInt(6);
                    Request r = new Request(new URL(url),request,response,order);
                    res.add(r);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } finally {
                if (rq != null)
                    rq.close();
                if (stmt != null)
                    stmt.close();
                if (c != null)
                    c.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
}
