package com.behsazan.model.adapters;

import com.behsazan.model.sqlite.SqliteHelper;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Created by admin on 07/31/2017.
 */
public class TableModelSequences extends AbstractTableModel {
    private Vector<Vector<Object>> data;
    private int columnCount;

    public TableModelSequences() {
        updateData();
    }

    public void updateData() {
        data = new Vector<Vector<Object>>();

        ResultSetMetaData metaData = null;
        try {
            ResultSet rs = new SqliteHelper().getAllSequences();
            metaData = rs.getMetaData();
            // names of columns

            columnCount = metaData.getColumnCount();
            // data of the table

            while (rs.next()) {
                Vector<Object> vector = new Vector<Object>();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    vector.add(rs.getObject(columnIndex));
                }
                data.add(vector);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getColumnName(int column) {
        return new String[]{
                "Id","Name","Number Of Requests","First Url","Last Url"
        }[column];
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(rowIndex).get(columnIndex);
    }
}
