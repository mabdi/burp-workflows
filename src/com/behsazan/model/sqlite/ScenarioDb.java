package com.behsazan.model.sqlite;

import com.behsazan.model.entity.Flow;
import com.behsazan.model.entity.Login;
import com.behsazan.model.entity.Scenario;

import java.sql.*;
import java.util.Vector;

/**
 * Created by admin on 08/24/2017.
 */
public class ScenarioDb extends SqliteHelper {



    public void insertScenario(Scenario scenario) throws SQLException {
        Connection c = getConnection();
        PreparedStatement stmt = c.prepareStatement("INSERT INTO SCENARIO " +
                "(PARAMS_JSON,URL,FLOW_ID,OUT_PARAM_NAME,NAME,DESCRIPTION) VALUES (?,?,?,?,?,?)");
        stmt.setString(1, scenario.getParams_json());
        stmt.setString(2, scenario.getUrl());
        stmt.setInt(3, scenario.getFlowId());
        stmt.setString(4, scenario.getOutParam());
        stmt.setString(5, scenario.getName());
        stmt.setString(6, scenario.getDescription());
        stmt.executeUpdate();

        stmt.close();
        c.close();
    }


    public Scenario getScenarioById(int id) throws SQLException {
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            c = getConnection();
            stmt = c.prepareStatement("SELECT ID,PARAMS_JSON,URL,FLOW_ID,OUT_PARAM_NAME,NAME,DESCRIPTION " +
                    " from SCENARIO WHERE ID= ?");
            stmt.setInt(1, id);
            rq = stmt.executeQuery();
            if (rq.next()) {
                String json = rq.getString(2);
                String url = rq.getString(3);
                int flowId = rq.getInt(4);
                String outParam = rq.getString(5);
                String name = rq.getString(6);
                String description = rq.getString(7);
                Flow flow = Flow.getById(flowId);
                Scenario r = new Scenario(id,name,description, json, outParam, url, flow);
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


    public void cloneScenario(int id) throws SQLException {
        Scenario log = getScenarioById(id);
        insertScenario(log);

    }


    public void deleteScenario(int id) throws SQLException {
        Connection c = null;
        c = getConnection();

        PreparedStatement stmt = c.prepareStatement("DELETE from SCENARIO WHERE ID =?");
        stmt.setInt(1, id);
        stmt.executeUpdate();

        c.close();

    }

    public Vector<Vector<Object>> getAllScenarios_Table() throws SQLException {
        Connection c = getConnection();
        Statement stmt = c.createStatement();

        ResultSet allRequests = stmt.executeQuery("SELECT SCENARIO.ID,SCENARIO.NAME,SCENARIO.DESCRIPTION,FLOW.NAME from SCENARIO" +
                " INNER JOIN FLOW ON FLOW.ID = SCENARIO.FLOW_ID");
        Vector<Vector<Object>> data = resultSetToVector(allRequests);
        allRequests.close();
        stmt.close();
        c.close();
        return data;
    }

    public void updateScenario(Scenario scenario) throws SQLException {

        Connection c = getConnection();
        PreparedStatement stmt = c.prepareStatement("UPDATE SCENARIO SET PARAMS_JSON = ?, URL=?, " +
                "FLOW_ID=?,OUT_PARAM_NAME=?,NAME=?,DESCRIPTION=?  WHERE ID =?");
        stmt.setString(1, scenario.getParams_json());
        stmt.setString(2, scenario.getUrl());
        stmt.setInt(3, scenario.getFlowId());
        stmt.setString(4, scenario.getOutParam());
        stmt.setString(5, scenario.getName());
        stmt.setString(6, scenario.getDescription());
        stmt.setInt(7, scenario.getId());
        stmt.executeUpdate();
        stmt.close();
        c.close();
    }
}
