package com.behsazan.model.sqlite;

import com.behsazan.model.entity.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by admin on 08/24/2017.
 */
public class FlowDb extends SqliteHelper {


    public Vector<Vector<Object>> getAllFlows_Table() throws SQLException {
        Connection c = getConnection();
        Statement stmt = c.createStatement();
        ResultSet allRequests = stmt.executeQuery("SELECT ID,NAME,SEQUENCE_COUNT,REQUEST_COUNT from FLOW");
        Vector<Vector<Object>> data = resultSetToVector(allRequests);
        allRequests.close();
        stmt.close();
        c.close();
        return data;
    }

    public void insertFlow(Flow flow) throws SQLException {
        Connection c = getConnection();
        try {
            c.setAutoCommit(false);
            PreparedStatement stmt = c.prepareStatement("INSERT INTO FLOW " +
                    "(NAME,SEQUENCE_COUNT,REQUEST_COUNT) VALUES (?,?,?)");
            stmt.setString(1, flow.getName());
            stmt.setInt(2, flow.getSeqs().size());
            int reqs = 0;
            for (Flow_Sequence tr : flow.getSeqs()) {
                reqs += tr.getRequests().size();
            }
            stmt.setInt(3, reqs);
            stmt.executeUpdate();
            ResultSet rs1 = c.createStatement().executeQuery("select last_insert_rowid();");
            rs1.next();
            int newId = rs1.getInt(1);
            flow.setId(newId);
            rs1.close();
            stmt.close();

            insertFlowParts(c, flow);

            c.commit();
            c.close();
        } catch (Exception ex) {
            c.rollback();
            throw ex;
        }
    }

    private void insertFlowParts(Connection c, Flow flow) throws SQLException {
        for (Flow_Sequence sq : flow.getSeqs()) {
            PreparedStatement stmt2 = c.prepareStatement("INSERT INTO FLOW_SEQUENCE " +
                    "(FLOW_ID,SEQUENCE_ID,URL,PATH_BASE1,PATH_BASE2,COOKIE) VALUES (?,?,?,?,?,?)");
            stmt2.setInt(1, flow.getId());
            stmt2.setInt(2, sq.getSequence().getId());
            stmt2.setString(3, sq.getUrl().toString());
            stmt2.setString(4, sq.getBase1());
            stmt2.setString(5, sq.getBase2());
            stmt2.setString(6, sq.getCookie());
            stmt2.executeUpdate();
            stmt2.close();

            ResultSet rs2 = c.createStatement().executeQuery("select last_insert_rowid();");
            rs2.next();
            int newId = rs2.getInt(1);
            sq.setId(newId);
            rs2.close();

            for (Flow_Request rq : sq.getRequests()) {
                PreparedStatement stmt3 = c.prepareStatement("INSERT INTO FLOW_REQUEST " +
                        "(FLOW_SEQUENCE_ID,REQUEST_ID,REQUEST,FLOW_ID) VALUES (?,?,?,?)");
                stmt3.setInt(1, sq.getId());
                stmt3.setInt(2, rq.getRequest().getId());
                stmt3.setBytes(3, rq.getModifiedRequest());
                stmt3.setInt(4, flow.getId());
                stmt3.executeUpdate();
                stmt3.close();

                ResultSet rs3 = c.createStatement().executeQuery("select last_insert_rowid();");
                rs3.next();
                newId = rs3.getInt(1);
                rs3.close();

                rq.setId(newId);

                for (RequestIn paramIn : rq.getInputParams()) {
                    PreparedStatement stmt4 = c.prepareStatement("INSERT INTO REQUEST_INPUT " +
                            "(FLOW_REQUEST_ID,PLACE_HOLDER,PARAM_PARAMS,PARAM_TYPE,FLOW_ID) VALUES (?,?,?,?,?)");
                    stmt4.setInt(1, rq.getId());
                    stmt4.setString(2, paramIn.getPlaceHoder());
                    stmt4.setString(3, paramIn.getTxtValue());
                    stmt4.setInt(4, paramIn.getType());
                    stmt4.setInt(5, flow.getId());
                    stmt4.executeUpdate();
                    stmt4.close();
                }
                for (ResponseOut paramOut : rq.getOutputParams()) {
                    PreparedStatement stmt4 = c.prepareStatement("INSERT INTO RESPONSE_OUTPUT " +
                            "(FLOW_REQUEST_ID,PARAM_NAME,PARAM_PARAMS,PARAM_TYPE,IS_GLOBAL,FLOW_ID) VALUES (?,?,?,?,?,?)");
                    stmt4.setInt(1, rq.getId());
                    stmt4.setString(2, paramOut.getName());
                    stmt4.setString(3, paramOut.getParam());
                    stmt4.setInt(4, paramOut.getType());
                    stmt4.setInt(5, (paramOut.isGlobal()) ? 1 : 0);
                    stmt4.setInt(6, flow.getId());
                    stmt4.executeUpdate();
                    stmt4.close();
                }
            }
        }
    }

    public void deleteFlow(int id) throws SQLException {
        Connection c = null;
        try {
            c = getConnection();
            c.setAutoCommit(false);
            PreparedStatement stmt = c.prepareStatement("DELETE from FLOW WHERE ID =?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            stmt = c.prepareStatement("DELETE from FLOW_SEQUENCE WHERE FLOW_ID =?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            stmt = c.prepareStatement("DELETE from FLOW_REQUEST WHERE FLOW_ID =?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            stmt = c.prepareStatement("DELETE from REQUEST_INPUT WHERE FLOW_ID =?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            stmt = c.prepareStatement("DELETE from RESPONSE_OUTPUT WHERE FLOW_ID =?");
            stmt.setInt(1, id);
            stmt.executeUpdate();

            c.commit();
            stmt.close();
            c.close();
        } catch (SQLException x) {
            if (c != null) {
                c.rollback();
            }
            throw x;
        }
    }



    public boolean isFlowNameUsed(String name) throws SQLException {
        Connection c = null;
        c = getConnection();
        PreparedStatement stmt = c.prepareStatement("SELECT COUNT(*) from FLOW WHERE NAME =?");
        stmt.setString(1, name);
        ResultSet allRequests = stmt.executeQuery();
        allRequests.next();
        boolean res = allRequests.getInt(1) > 0;
        allRequests.close();
        stmt.close();
        c.close();
        return res;
    }

    public Flow getFlowById(int id) throws SQLException {
        Flow res = null;
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            c = getConnection();
            stmt = c.prepareStatement("SELECT ID,NAME,SEQUENCE_COUNT,REQUEST_COUNT,DESCRIPTION from FLOW WHERE ID=?");
            stmt.setInt(1, id);
            rq = stmt.executeQuery();
            if (!rq.next()) {
                res = null;
            } else {
                String name = rq.getString(2);
                String descr = rq.getString(5);
                List<Flow_Sequence> reqs = getFlowSequenceById(id);
                Flow s = new Flow(name, descr, reqs);
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


    public Flow getFlowByName(String selectedflow) throws SQLException {
        Flow res = null;
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            c = getConnection();
            stmt = c.prepareStatement("SELECT ID,NAME,SEQUENCE_COUNT,REQUEST_COUNT,DESCRIPTION from FLOW WHERE NAME=?");
            stmt.setString(1, selectedflow);
            rq = stmt.executeQuery();
            if (!rq.next()) {
                res = null;
            } else {
                int id = rq.getInt(1);
                String descr = rq.getString(5);
                List<Flow_Sequence> reqs = getFlowSequenceById(id);
                Flow s = new Flow(selectedflow, descr, reqs);
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

    private List<Flow_Sequence> getFlowSequenceById(int id) throws SQLException {
        List<Flow_Sequence> res = new ArrayList<>();
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            c = getConnection();
            stmt = c.prepareStatement("SELECT ID,FLOW_ID,SEQUENCE_ID,URL,PATH_BASE1,PATH_BASE2,COOKIE " +
                    "from FLOW_SEQUENCE WHERE FLOW_ID= ?");
            stmt.setInt(1, id);
            rq = stmt.executeQuery();
            while (rq.next()) {
                int record_id = rq.getInt(1);
                int sid = rq.getInt(3);
                String url = rq.getString(4);
                String base1 = rq.getString(5);
                String base2 = rq.getString(6);
                String cookie = rq.getString(7);

                Sequence sequence = Sequence.getById(sid);
                List<Flow_Request> reqs = getFlowRequestById(record_id);
                Flow_Sequence r = new Flow_Sequence(record_id, sequence, new URL(url), base1, base2, cookie, reqs);

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
        return res;
    }

    private List<Flow_Request> getFlowRequestById(int flowSequenceId) throws SQLException {
        List<Flow_Request> res = new ArrayList<>();
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            c = getConnection();
            stmt = c.prepareStatement("SELECT ID,FLOW_SEQUENCE_ID,REQUEST_ID,REQUEST  from FLOW_REQUEST " +
                    "WHERE FLOW_SEQUENCE_ID = ?");
            stmt.setInt(1, flowSequenceId);
            rq = stmt.executeQuery();
            while (rq.next()) {
                int record_id = rq.getInt(1);
                int request_id = rq.getInt(3);
                byte[] data = rq.getBytes(4);
                List<RequestIn> inputParams = getRequestInFor(record_id);
                List<ResponseOut> outputParams = getResponseOutFor(record_id);
                Request request = Request.getById(request_id);
                Flow_Request r = new Flow_Request(record_id, request, inputParams, outputParams, data);
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

    private List<ResponseOut> getResponseOutFor(int record_id) throws SQLException {
        List<ResponseOut> res = new ArrayList<>();
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;

        try {
            c = getConnection();
            stmt = c.prepareStatement("SELECT ID,FLOW_REQUEST_ID,IS_GLOBAL,PARAM_NAME,PARAM_PARAMS,PARAM_TYPE  " +
                    "from RESPONSE_OUTPUT WHERE FLOW_REQUEST_ID = ?");
            stmt.setInt(1, record_id);
            rq = stmt.executeQuery();
            while (rq.next()) {
                int id = rq.getInt(1);
                int type = rq.getInt(6);
                int isGlobal = rq.getInt(3);
                String params = rq.getString(5);
                String name = rq.getString(4);
                ResponseOut r = new ResponseOut(id, type, name, params, isGlobal == 1);
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

    private List<RequestIn> getRequestInFor(int record_id) throws SQLException {
        List<RequestIn> res = new ArrayList<>();
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            c = getConnection();
            stmt = c.prepareStatement("SELECT ID,FLOW_REQUEST_ID,PLACE_HOLDER,PARAM_PARAMS,PARAM_TYPE  " +
                    "from REQUEST_INPUT WHERE FLOW_REQUEST_ID = ?");
            stmt.setInt(1, record_id);
            rq = stmt.executeQuery();
            while (rq.next()) {
                int id = rq.getInt(1);
                int type = rq.getInt(5);
                String params = rq.getString(4);
                String name = rq.getString(3);
                RequestIn r = new RequestIn(id, type, name, params);
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

    public void updateFlow(Flow flow) throws SQLException {
        Connection c = null;
        try {
            c = getConnection();
            c.setAutoCommit(false);
            PreparedStatement stmt = c.prepareStatement(
                    "UPDATE FLOW SET NAME = ?,DESCRIPTION=?,SEQUENCE_COUNT=?,REQUEST_COUNT=?  " +
                            " WHERE ID =?");
            stmt.setString(1, flow.getName());
            stmt.setString(1, flow.getDescription());
            stmt.setInt(3, flow.getSeqs().size());
            int reqs = 0;
            for (Flow_Sequence tr : flow.getSeqs()) {
                reqs += tr.getRequests().size();
            }
            stmt.setInt(4, reqs);
            stmt.executeUpdate();
            stmt.close();

            stmt = c.prepareStatement("DELETE from FLOW_SEQUENCE WHERE FLOW_ID =?");
            stmt.setInt(1, flow.getId());
            stmt.executeUpdate();
            stmt = c.prepareStatement("DELETE from FLOW_REQUEST WHERE FLOW_ID =?");
            stmt.setInt(1, flow.getId());
            stmt.executeUpdate();
            stmt = c.prepareStatement("DELETE from REQUEST_INPUT WHERE FLOW_ID =?");
            stmt.setInt(1, flow.getId());
            stmt.executeUpdate();
            stmt = c.prepareStatement("DELETE from RESPONSE_OUTPUT WHERE FLOW_ID =?");
            stmt.setInt(1, flow.getId());
            stmt.executeUpdate();

            insertFlowParts(c, flow);
            c.commit();
            c.close();
        } catch (Exception e) {
            if (c != null) {
                c.rollback();
            }
            throw e;
        }
    }


    public void cloneFlow(int id, String newName) throws SQLException {
        Flow tcase = getFlowById(id);

        tcase.setName(newName);
        insertFlow(tcase);

    }


    public List<Flow> getAllFlow() throws SQLException {
        List<Flow> res = new ArrayList<>();
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            c = getConnection();
            stmt = c.prepareStatement("SELECT ID from FLOW");
            rq = stmt.executeQuery();
            while (rq.next()) {
                int id = rq.getInt(1);
                Flow t = getFlowById(id);
                res.add(t);
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

    public List<String> getAllFlowName() throws SQLException {
        List<String> res = new ArrayList<>();
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            c = getConnection();
            stmt = c.prepareStatement("SELECT NAME from FLOW");
            rq = stmt.executeQuery();
            while (rq.next()) {
                res.add(rq.getString(1));
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
