package com.behsazan.model.adapters;

import com.behsazan.model.entity.Script;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by admin on 07/31/2017.
 */
public class TableModelScriptsSelect extends AbstractTableModel {
    private List<Script> data;
    private String[] columns = new String[]{
        "Id","Name","Type","Language"
    };

    public TableModelScriptsSelect() {
        data = new ArrayList<>();
    }

    public List<Script> getData() {
        return data;
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
        if(columnIndex == 0){
            return data.get(rowIndex).getId();
        }
        if(columnIndex == 1){
            return data.get(rowIndex).getName();
        }
        if(columnIndex == 2){
            int val = (int) data.get(rowIndex).getType();
            return Script.getTypesString().get(val);
        }
        if(columnIndex == 3){
            int val = (int) data.get(rowIndex).getLang();
            return Script.getLangsString().get(val);
        }
        return "";
    }

    public void loadData() {
        data = Script.getAll();
    }
}
