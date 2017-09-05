package com.behsazan.model.adapters;

import com.behsazan.model.entity.Script;
import com.behsazan.model.entity.Sequence;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

/**
 * Created by admin on 07/31/2017.
 */
public class TableModelScripts extends AbstractTableModel {
    private Vector<Vector<Object>> data;
    private String[] columns = new String[]{
        "Id","Name","Type","Language"
    };

    public TableModelScripts() {
        updateData();
    }

    public void updateData() {
        data = Script.getAll_Table();
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
        if(columnIndex == 2){
            int val = (int) data.get(rowIndex).get(columnIndex);
            return Script.getTypesString().get(val);
        }
        if(columnIndex == 3){
            int val = (int) data.get(rowIndex).get(columnIndex);
            return Script.getLangsString().get(val);
        }
        return data.get(rowIndex).get(columnIndex);
    }
}
