package com.behsazan.model.sqlite;

import burp.BurpExtender;
import com.behsazan.model.DataUtils;
import com.behsazan.model.entity.*;

import javax.sql.rowset.serial.SerialBlob;
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
    public static final int DB_VERSION = 2;
    private List<Connection> connections = new ArrayList<>();


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


    public File getDbFile() {
        String appHome = DataUtils.getAppHome();
        return new File(appHome,"data.db");
    }

    private Connection getConnection() throws  SQLException {
        File db = getDbFile();
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection c = DriverManager.getConnection("jdbc:sqlite:"+db.getAbsolutePath());
        connections.add(c);
        return c;
    }

    public void closeAllConnection(){
        BurpExtender.logText("Dangling Connections Count: " + connections.size());
        for (Connection c :
                connections) {
            if (c != null){
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
        if(ver<1){
            update_v1(stmt);
            ver++;
        }
        if(ver<2){
            update_v2(stmt);
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

    }

    private void update_v2(Statement stmt) throws SQLException {

        String createTableTestCase = "DROP TABLE IF EXISTS TESTCASE;"+
                " CREATE TABLE TESTCASE " +
                "(ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                " NAME           TEXT    NOT NULL, " +
                " SEQUENCE_COUNT            INTEGER     NOT NULL, " +
                " REQUEST_COUNT            INTEGER     NOT NULL  " +
                " )";
        String createTableTestCaseSequence = "DROP TABLE IF EXISTS TESTCASE_SEQUENCE;"+
                " CREATE TABLE TESTCASE_SEQUENCE " +
                "(ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                " TESTCASE_ID           INTEGER    NOT NULL, " +
                " SEQUENCE_ID           INTEGER    NOT NULL, " +
                " URL           TEXT    NOT NULL, " +
                " PATH_BASE1           TEXT    NOT NULL, " +
                " PATH_BASE2           TEXT    NOT NULL, " +
                " COOKIE           TEXT    NOT NULL " +
                " )";
        String createTableTestCaseRequest = "DROP TABLE IF EXISTS TESTCASE_REQUEST;"+
                " CREATE TABLE TESTCASE_REQUEST " +
                "(ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                " TESTCASE_SEQUENCE_ID           INTEGER    NOT NULL, " +
                " REQUEST_ID           INTEGER    NOT NULL, " +
                " REQUEST           BLOB    NOT NULL " +
                " )";
        String createTableRequestIn = "DROP TABLE IF EXISTS REQUEST_INPUT;"+
                " CREATE TABLE REQUEST_INPUT " +
                "(ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                " TESTCASE_REQUEST_ID           INTEGER    NOT NULL, " +
                " PLACE_HOLDER        TEXT NOT NULL, " +
                " PARAM_PARAMS        TEXT NOT NULL, " +
                " PARAM_TYPE      INTEGER NOT NULL " +
                " )";
        String createTableResponseOut = "DROP TABLE IF EXISTS RESPONSE_OUTPUT;"+
                " CREATE TABLE RESPONSE_OUTPUT " +
                "(ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                " TESTCASE_REQUEST_ID           INTEGER    NOT NULL, " +
                " IS_GLOBAL        INTEGER NOT NULL, " +
                " PARAM_NAME        TEXT NOT NULL, " +
                " PARAM_PARAMS        TEXT NOT NULL, " +
                " PARAM_TYPE      INTEGER NOT NULL " +
                " )";
        stmt.executeUpdate(createTableTestCase);
        stmt.executeUpdate(createTableTestCaseSequence);
        stmt.executeUpdate(createTableTestCaseRequest);
        stmt.executeUpdate(createTableRequestIn);
        stmt.executeUpdate(createTableResponseOut);
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

    public Vector<Vector<Object>> getAllSequences_Table() throws SQLException {
        Connection c = getConnection();
        Statement stmt = c.createStatement();
        ResultSet allRequests = stmt.executeQuery("SELECT ID as Id,NAME as Name,REQUEST_COUNT as 'Number Of Requests',"+
                " FIRST_URL as 'First Url',LAST_URL as 'Last Url' from SEQUENCE");
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


    public Vector<Vector<Object>> getAllTestCases_Table() throws SQLException {
        Connection c = getConnection();
        Statement stmt = c.createStatement();
        ResultSet allRequests = stmt.executeQuery("SELECT ID,NAME,SEQUENCE_COUNT,REQUEST_COUNT from TESTCASE");
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
            stmt = c.prepareStatement("DELETE from REQUEST WHERE SID =?");
            stmt.setInt(1,id);
            stmt.executeUpdate();
            stmt.close();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isPossibleToDeleteSequence(int id){
        Connection c = null;
        try {
            c = getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT COUNT(*) from TESTCASE_SEQUENCE WHERE SID =?");
            stmt.setInt(1,id);
            ResultSet allRequests = stmt.executeQuery();
            allRequests.next();
            boolean res = allRequests.getInt(1)==0;
            allRequests.close();
            stmt.close();
            c.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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

    public List<Sequence> getAllSequences() {
        List<Sequence> res = new ArrayList<>();
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            try {
                c = getConnection();
                stmt = c.prepareStatement("SELECT ID,NAME,REQUEST_COUNT,FIRST_URL,LAST_URL from SEQUENCE");
                rq = stmt.executeQuery();
                while (rq.next()){
                    int id = rq.getInt(1);
                    String name = rq.getString(2);
                    List<Request> reqs = getSequenceRequestById(id);
                    Sequence s = new Sequence(name, reqs);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
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
                    r.setId(rq.getInt(1));
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

    public void updateRequestRequest(int id, byte[] message) {
        try {
            Connection c = getConnection();
            PreparedStatement stmt = c.prepareStatement("UPDATE REQUEST SET REQUEST = ? WHERE ID =?");
            stmt.setBytes(1,message);
            stmt.setInt(2,id);
            stmt.executeUpdate();
            stmt.close();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRequestResponse(int id, byte[] message) {
        try {
            Connection c = getConnection();
            PreparedStatement stmt = c.prepareStatement("UPDATE REQUEST SET RESPONSE = ? WHERE ID =?");
            stmt.setBytes(1,message);
            stmt.setInt(2,id);
            stmt.executeUpdate();
            stmt.close();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cloneSequence(int id, String response) {
        Sequence seq = getSequenceById(id);
        seq.setName(response);
        try {
            insertSequence(seq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertTestCase(TestCase testCase) throws SQLException {
        Connection c = getConnection();
        try {
            c.setAutoCommit(false);
            PreparedStatement stmt = c.prepareStatement("INSERT INTO TESTCASE "+
                    "(NAME,SEQUENCE_COUNT,REQUEST_COUNT) VALUES (?,?,?)");
            stmt.setString(1, testCase.getName());
            stmt.setInt(2, testCase.getReqs().size());
            int reqs = 0;
            for (TestCase_Sequence tr : testCase.getReqs()) {
                reqs += tr.getRequests().size();
            }
            stmt.setInt(3, reqs);
            stmt.executeUpdate();
            ResultSet rs1 = c.createStatement().executeQuery("select last_insert_rowid();");
            rs1.next();
            int newId = rs1.getInt(1);
            testCase.setId(newId);
            rs1.close();
            stmt.close();
            for (TestCase_Sequence sq : testCase.getReqs()) {
                PreparedStatement stmt2 = c.prepareStatement("INSERT INTO TESTCASE_SEQUENCE "+
                        "(TESTCASE_ID,SEQUENCE_ID,URL,PATH_BASE1,PATH_BASE2,COOKIE) VALUES (?,?,?,?,?,?)");
                stmt2.setInt(1, testCase.getId());
                stmt2.setInt(2, sq.getSequence().getId());
                stmt2.setString(3, sq.getUrl().toString());
                stmt2.setString(4, sq.getBase1());
                stmt2.setString(5, sq.getBase2());
                stmt2.setString(6, sq.getCookie());
                stmt2.executeUpdate();
                stmt2.close();

                ResultSet rs2 = c.createStatement().executeQuery("select last_insert_rowid();");
                rs2.next();
                newId = rs2.getInt(1);
                sq.setId(newId);
                rs2.close();

                for (TestCase_Request rq : sq.getRequests()) {
                    PreparedStatement stmt3 = c.prepareStatement("INSERT INTO TESTCASE_REQUEST "+
                            "(TESTCASE_SEQUENCE_ID,REQUEST_ID,REQUEST) VALUES (?,?,?)");
                    stmt3.setInt(1, testCase.getId());
                    stmt3.setInt(2, sq.getId());
                    stmt3.setBytes(3, rq.getModifiedRequest());
                    stmt3.executeUpdate();
                    stmt3.close();

                    ResultSet rs3 = c.createStatement().executeQuery("select last_insert_rowid();");
                    rs3.next();
                    newId = rs3.getInt(1);
                    rs3.close();

                    rq.setId(newId);

                    for (RequestIn paramIn: rq.getInputParams()) {
                        PreparedStatement stmt4 = c.prepareStatement("INSERT INTO REQUEST_INPUT "+
                                "(RSID,PLACE_HOLDER,PARAM_PARAMS,PARAM_TYPE) VALUES (?,?,?,?)");
                        stmt4.setInt(1, rq.getId());
                        stmt4.setString(2, paramIn.getPlaceHoder());
                        stmt4.setString(3, paramIn.getTxtValue());
                        stmt4.setInt(4, paramIn.getType());
                        stmt4.executeUpdate();
                        stmt4.close();
                    }
                    for (ResponseOut paramOut: rq.getOutputParams()) {
                        PreparedStatement stmt4 = c.prepareStatement("INSERT INTO RESPONSE_OUTPUT "+
                                "(RSID,PARAM_NAME,PARAM_PARAMS,PARAM_TYPE,IS_GLOBAL) VALUES (?,?,?,?,?)");
                        stmt4.setInt(1, rq.getId());
                        stmt4.setString(2, paramOut.getName());
                        stmt4.setString(3, paramOut.getParam());
                        stmt4.setInt(4, paramOut.getType());
                        stmt4.setInt(5,(paramOut.isGlobal())?1:0);
                        stmt4.executeUpdate();
                        stmt4.close();
                    }
                }
            }
            c.commit();
            c.close();
        }catch (Exception ex){
            c.rollback();
            throw ex;
        }
    }

    public List<String> getAllVariables(boolean globals) {
        List<String> res = new ArrayList<>();
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            try {
                c = getConnection();
                stmt = c.prepareStatement("SELECT PARAM_NAME from RESPONSE_OUTPUT WHERE IS_GLOBAL = ?");
                stmt.setInt(1,(globals?1:0));
                rq = stmt.executeQuery();
                while (rq.next()){
                    String name = rq.getString(1);
                    res.add(name);
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

    public void deleteTestCase(int id) {
        // TODO -- delete all dependent request and sequences
    }

    public boolean isTestCaseNameUsed(String name) {
        Connection c = null;
        try {
            c = getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT COUNT(*) from TESTCASE WHERE NAME =?");
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

    public TestCase getTestCaseById(int id) {
        TestCase res = null;
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            try {
                c = getConnection();
                stmt = c.prepareStatement("SELECT ID,NAME,SEQUENCE_COUNT,REQUEST_COUNT from TESTCASE WHERE ID=?");
                stmt.setInt(1, id);
                rq = stmt.executeQuery();
                if (!rq.next()){
                    res = null;
                }else {
                    String name = rq.getString(2);
                    List<TestCase_Sequence> reqs = getTestCaseSequenceById(id);
                    TestCase s = new TestCase(name, reqs);
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

    private List<TestCase_Sequence> getTestCaseSequenceById(int id) {
        List<TestCase_Sequence> res = new ArrayList<>();
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            try {
                c = getConnection();
                stmt = c.prepareStatement("SELECT ID,TESTCASE_ID,SEQUENCE_ID,URL,PATH_BASE1,PATH_BASE2,COOKIE "+
                        "from TESTCASE_SEQUENCE WHERE TESTCASE_ID= ?");
                stmt.setInt(1, id);
                rq = stmt.executeQuery();
                while (rq.next()){
                    int record_id = rq.getInt(1);
                    int sid = rq.getInt(3);
                    String url = rq.getString(4);
                    String base1 = rq.getString(5);
                    String base2 = rq.getString(6);
                    String cookie = rq.getString(7);

                    Sequence sequence = getSequenceById(sid);
                    List<TestCase_Request> reqs = getTestCaseRequestById(record_id);
                    TestCase_Sequence r = new TestCase_Sequence(record_id,sequence, new URL(url),base1,base2,cookie,reqs);

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

    private List<TestCase_Request> getTestCaseRequestById(int testCaseSequenceId) {
        List<TestCase_Request> res = new ArrayList<>();
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            try {
                c = getConnection();
                stmt = c.prepareStatement("SELECT ID,TESTCASE_SEQUENCE_ID,REQUEST_ID,REQUEST  from TESTCASE_REQUEST "+
                        "WHERE TESTCASE_SEQUENCE_ID = ?");
                stmt.setInt(1, testCaseSequenceId);
                rq = stmt.executeQuery();
                while (rq.next()){
                    int record_id = rq.getInt(1);
                    int request_id = rq.getInt(3);
                    byte[] data = rq.getBytes(4);
                    List<RequestIn> inputParams = getRequestInFor(record_id);
                    List<ResponseOut> outputParams = getResponseOutFor(record_id);
                    Request request = getRequestById(request_id);
                    TestCase_Request r = new TestCase_Request(record_id,request,inputParams,outputParams,data);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    private List<ResponseOut> getResponseOutFor(int record_id) {
        List<ResponseOut> res = new ArrayList<>();
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            try {
                c = getConnection();
                stmt = c.prepareStatement("SELECT ID,TESTCASE_REQUEST_ID,IS_GLOBAL,PARAM_NAME,PARAM_PARAMS,PARAM_TYPE  "+
                        "from RESPONSE_OUTPUT WHERE TESTCASE_REQUEST_ID = ?");
                stmt.setInt(1, record_id);
                rq = stmt.executeQuery();
                while (rq.next()){
                    int id = rq.getInt(1);
                    int type = rq.getInt(6);
                    int isGlobal = rq.getInt(3);
                    String params = rq.getString(5);
                    String name = rq.getString(4);
                    ResponseOut r = new ResponseOut(id,type,name,params,isGlobal==1);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    private List<RequestIn> getRequestInFor(int record_id) {
        List<RequestIn> res = new ArrayList<>();
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            try {
                c = getConnection();
                stmt = c.prepareStatement("SELECT ID,TESTCASE_REQUEST_ID,PLACE_HOLDER,PARAM_PARAMS,PARAM_TYPE  "+
                        "from REQUEST_INPUT WHERE TESTCASE_REQUEST_ID = ?");
                stmt.setInt(1, record_id);
                rq = stmt.executeQuery();
                while (rq.next()){
                    int id = rq.getInt(1);
                    int type = rq.getInt(5);
                    String params = rq.getString(4);
                    String name = rq.getString(3);
                    RequestIn r = new RequestIn(id,type,name,params);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public Request getRequestById(int Id) {
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            try {
                c = getConnection();
                stmt = c.prepareStatement("SELECT ID,SID,REQUEST,RESPONSE,URL,ORDER_NUM from REQUEST WHERE ID= ?");
                stmt.setInt(1, Id);
                rq = stmt.executeQuery();
                if (rq.next()){
                    String url = rq.getString(5);
                    byte[] request = rq.getBytes(3);
                    byte[] response = rq.getBytes(4);
                    int order = rq.getInt(6);
                    Request r = new Request(new URL(url),request,response,order);
                    r.setId(rq.getInt(1));
                    return  r;
                }else{
                    return null;
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
        return null;
    }
}
