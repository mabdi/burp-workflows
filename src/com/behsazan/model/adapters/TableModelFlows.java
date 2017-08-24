package com.behsazan.model.adapters;

import com.behsazan.model.entity.Flow;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

/**
 * Created by admin on 07/31/2017.
 */
public class TableModelFlows extends AbstractTableModel {
    private Vector<Vector<Object>> data;
    private String[] columns = new String[]{
        "Id","Name","#Sequence","#Request"
    };

    public TableModelFlows() {
        updateData();
    }

    public void updateData() {
        data = Flow.getAllFlows_Table();
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
