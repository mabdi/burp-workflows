package com.behsazan.model.adapters;

import com.behsazan.model.sqlite.SqliteHelper;

import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

/**
 * Created by admin on 07/31/2017.
 */
public class TableModelLogins extends AbstractTableModel {
    private Vector<Vector<Object>> data;
    private String[] columns = new String[]{
        "Id","username","password","OutParam","SequenceName","Last Login"
    };

    public TableModelLogins() {
        updateData();
    }

    public void updateData() {
        try {
            data = new SqliteHelper().getAllLogins_Table();
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
        if(columnIndex == 5){
            Integer lasttime = (Integer) data.get(rowIndex).get(columnIndex);
            Date dt = new Date();
            if((dt.getTime() - lasttime)/(1000*60) > 30){
                return "Long time ago";
            }else{
                return (dt.getTime() - lasttime) + " min ago";
            }
        }
        return data.get(rowIndex).get(columnIndex);
    }
}
