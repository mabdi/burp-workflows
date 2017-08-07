package com.behsazan.model.adapters;

import com.behsazan.model.sqlite.SqliteHelper;

import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Created by admin on 07/31/2017.
 */
public class TableModelTestCases extends AbstractTableModel {
    private Vector<Vector<Object>> data;
    private String[] columns = new String[]{
        "Id","Name","#Sequence","#Request"
    };

    public TableModelTestCases() {
        updateData();
    }

    public void updateData() {
        try {
            data = new SqliteHelper().getAllTestCases_Table();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(rowIndex).get(columnIndex);
    }
}
