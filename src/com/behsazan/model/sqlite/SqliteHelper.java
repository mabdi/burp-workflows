package com.behsazan.model.sqlite;

import com.behsazan.model.DataUtils;
import com.behsazan.model.entity.Request;
import com.behsazan.model.entity.Sequence;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.*;

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
                " URL        TEXT NOT NULL " +
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
        for (Request rq :sq.getRequest()) {
            PreparedStatement  stmt2 = c.prepareStatement("INSERT INTO REQUEST (SID,REQUEST,RESPONSE,URL) VALUES (?,?,?,?)");
            stmt2.setInt(1,rq.getSequence().getId());
            stmt2.setBytes(2,rq.getRequest());
            stmt2.setBytes(3,rq.getResponse());
            stmt2.setString(4,rq.getUrl().toString());
            stmt2.executeUpdate();
            stmt2.close();
        }
        stmt.close();
        c.close();
    }

    public ResultSet getAllSequences() throws SQLException {
        Connection c = getConnection();
        Statement stmt = c.createStatement();
        ResultSet allRequests = stmt.executeQuery("SELECT ID as Id,NAME as Name,REQUEST_COUNT as 'Number Of Requests',FIRST_URL as 'First Url',LAST_URL as 'Last Url' from SEQUENCE");

        return allRequests;
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
}
