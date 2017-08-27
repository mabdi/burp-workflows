package com.behsazan.model.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 08/27/2017.
 */
public class SettingDb extends SqliteHelper {

    public void updateKey(String key,String value) throws SQLException {
        Connection c = getConnection();
        PreparedStatement stmt = c.prepareStatement("UPDATE SETTING SET VAR_VALUE = ? WHERE VAR_KEY =?");
        stmt.setString(1, value);
        stmt.setString(2, key);
        int rows = stmt.executeUpdate();
        if(rows == 0){
            stmt = c.prepareStatement("INSERT INTO SETTING (VAR_KEY,VAR_VALUE) VALUES (?,?)");
            stmt.setString(1, key);
            stmt.setString(2, value);
            stmt.executeUpdate();
        }
        stmt.close();
        c.close();
    }


    public String loadSettings(String key) throws SQLException{
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rq = null;
        try {
            c = getConnection();
            stmt = c.prepareStatement("SELECT VAR_KEY,VAR_VALUE from SETTING WHERE VAR_KEY = ?");
            stmt.setString(1,key);
            rq = stmt.executeQuery();
            if (rq.next()) {
                return rq.getString(2);
            }
        } finally {
            if (rq != null)
                rq.close();
            if (stmt != null)
                stmt.close();
            if (c != null)
                c.close();
        }
        return null;
    }
}
