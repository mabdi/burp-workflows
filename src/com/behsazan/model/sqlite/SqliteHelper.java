package com.behsazan.model.sqlite;

import burp.BurpExtender;
import com.behsazan.model.DataUtils;
import com.behsazan.model.entity.*;

import java.io.File;
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
    public static final int DB_VERSION = 4;
    private List<Connection> connections = new ArrayList<>();


    public void initDb() throws SQLException {
        Connection c = null;
        try {
            c = getConnection();
            c.setAutoCommit(false);
            Statement stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='APPMETA'");
            if (!rs.next()) {
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
            if (rs.next()) {
                ver = Integer.parseInt(rs.getString("VALUE"));
            }
            if (ver != DB_VERSION) {
                update_Db(stmt, ver, DB_VERSION);
            }
            stmt.executeUpdate("UPDATE APPMETA " +
                    "SET VALUE = '" + DB_VERSION + "' WHERE NAME = 'DB_VERSION'");
            c.commit();
            c.close();
        } catch (Exception e) {
            if (c != null) {
                c.rollback();
            }
            throw e;
        }
    }


    public File getDbFile() {
        String appHome = DataUtils.getAppHome();
        return new File(appHome, "data.db");
    }

    protected Connection getConnection() throws SQLException {
        File db = getDbFile();
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection c = DriverManager.getConnection("jdbc:sqlite:" + db.getAbsolutePath());
        connections.add(c);
        return c;
    }

    public void closeAllConnection() {
        BurpExtender.logText("Dangling Connections Count: " + connections.size());
        for (Connection c :
                connections) {
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace(BurpExtender.getInstance().getStdout());
                }
            }
        }
        connections.clear();
    }

    private void update_Db(Statement stmt, int fromVer, int toVer) throws SQLException {
        int ver = fromVer;
        if (ver < 1) {
            update_v1(stmt);
            ver++;
        }
        if (ver < 2) {
            update_v2(stmt);
            ver++;
        }
        if (ver < 3) {
            update_v3(stmt);
            ver++;
        }
        if (ver < 4) {
            update_v4(stmt);
            ver++;
        }
    }

    private void update_v1(Statement stmt) throws SQLException {

        String createTableSquence = "CREATE TABLE SEQUENCE " +
                "(ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                " NAME           TEXT    NOT NULL, " +
                " DESCRIPTION           TEXT    NOT NULL, " +
                " BASE           TEXT    NOT NULL, " +
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

    }

    private void update_v2(Statement stmt) throws SQLException {

        String createTableFlow = "DROP TABLE IF EXISTS FLOW;" +
                " CREATE TABLE FLOW " +
                "(ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                " NAME           TEXT    NOT NULL, " +
                " DESCRIPTION           TEXT    NOT NULL, " +
                " PARAMETERS           TEXT    NOT NULL, " +
                " SEQUENCE_COUNT            INTEGER     NOT NULL, " +
                " REQUEST_COUNT            INTEGER     NOT NULL  " +
                " )";
        String createTableFlowSequence = "DROP TABLE IF EXISTS FLOW_SEQUENCE;" +
                " CREATE TABLE FLOW_SEQUENCE " +
                "(ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                " FLOW_ID           INTEGER    NOT NULL, " +
                " SEQUENCE_ID           INTEGER    NOT NULL, " +
                " URL           TEXT    NOT NULL, " +
                " PATH_BASE1           TEXT    NOT NULL, " +
                " PATH_BASE2           TEXT    NOT NULL, " +
                " COOKIE           TEXT    NOT NULL " +
                " )";
        String createTableFlowRequest = "DROP TABLE IF EXISTS FLOW_REQUEST;" +
                " CREATE TABLE FLOW_REQUEST " +
                "(ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                " FLOW_ID           INTEGER    NOT NULL, " +
                " FLOW_SEQUENCE_ID           INTEGER    NOT NULL, " +
                " REQUEST_ID           INTEGER    NOT NULL, " +
                " REQUEST           BLOB    NOT NULL " +
                " )";
        String createTableRequestIn = "DROP TABLE IF EXISTS REQUEST_INPUT;" +
                " CREATE TABLE REQUEST_INPUT " +
                "(ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                " FLOW_ID           INTEGER    NOT NULL, " +
                " FLOW_REQUEST_ID           INTEGER    NOT NULL, " +
                " PLACE_HOLDER        TEXT NOT NULL, " +
                " PARAM_PARAMS        TEXT NOT NULL, " +
                " PARAM_TYPE      INTEGER NOT NULL " +
                " )";
        String createTableResponseOut = "DROP TABLE IF EXISTS RESPONSE_OUTPUT;" +
                " CREATE TABLE RESPONSE_OUTPUT " +
                "(ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                " FLOW_ID           INTEGER    NOT NULL, " +
                " FLOW_REQUEST_ID           INTEGER    NOT NULL, " +
                " IS_GLOBAL        INTEGER NOT NULL, " +
                " PARAM_NAME        TEXT NOT NULL, " +
                " PARAM_PARAMS        TEXT NOT NULL, " +
                " PARAM_TYPE      INTEGER NOT NULL " +
                " )";
        stmt.executeUpdate(createTableFlow);
        stmt.executeUpdate(createTableFlowSequence);
        stmt.executeUpdate(createTableFlowRequest);
        stmt.executeUpdate(createTableRequestIn);
        stmt.executeUpdate(createTableResponseOut);
    }

    private void update_v3(Statement stmt) throws SQLException {
        String createTableGlobals = "DROP TABLE IF EXISTS GLOBAL_VARIABLES;" +
                "CREATE TABLE GLOBAL_VARIABLES " +
                "(KEY  TEXT PRIMARY KEY," +
                " VALUE         TEXT NOT NULL)";
        String createTableLogins = "DROP TABLE IF EXISTS LOGIN;" +
                "CREATE TABLE LOGIN " +
                "(ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                " USER_NAME        TEXT NOT NULL, " +
                " PASSWORD        TEXT NOT NULL, " +
                " URL        TEXT NOT NULL, " +
                " BASE        TEXT NOT NULL, " +
                " FLOW_ID        INTEGER NOT NULL, " +
                " OUT_PARAM_NAME        TEXT NOT NULL, " +
                " SESSION_VALUE        TEXT NOT NULL, " +
                " SESSION_CREATE_TIME         INTEGER NOT NULL)";
        String createTableSuite = "DROP TABLE IF EXISTS SUITE;" +
                "CREATE TABLE SUITE " +
                "(ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                " NAME        TEXT NOT NULL, " +
                " DESCRIPTION        TEXT NOT NULL, " +
                " CATEGORY        TEXT NOT NULL  " +
                ")";

//        String alterTableSequenceAddDescription = "ALTER TABLE SEQUENCE ADD COLUMN DESCRIPTION TEXT";


        stmt.executeUpdate(createTableGlobals);
        stmt.executeUpdate(createTableLogins);
        stmt.executeUpdate(createTableSuite);
    }

    private void update_v4(Statement stmt) throws SQLException {
        stmt.executeUpdate("DROP TABLE IF EXISTS TESTCASE;");
        stmt.executeUpdate("DROP TABLE IF EXISTS TESTCASE_REQUEST;");
        stmt.executeUpdate("DROP TABLE IF EXISTS TESTCASE_SEQUENCE;");
        update_v2(stmt);
        update_v3(stmt);
    }

    protected static Vector<Vector<Object>> resultSetToVector(ResultSet allRequests) throws SQLException {
        int columnCount = allRequests.getMetaData().getColumnCount();
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (allRequests.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(allRequests.getObject(columnIndex));
            }
            data.add(vector);
        }
        return data;
    }

}
