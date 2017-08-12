package com.behsazan.model.adapters;

import com.behsazan.model.entity.ResponseOut;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 08/08/2017.
 */
public class TableModelResponseOut extends AbstractTableModel {
    public static final String[] COLUMNS = new String[]{
            "Id","Name","Type","Regex"
    };
    private List<ResponseOut> data;

    public TableModelResponseOut() {
        this.data = new ArrayList<>();
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ResponseOut elm = data.get(rowIndex);
        switch (columnIndex){
            case 0:
                return elm.getId();
            case 1:
                return elm.getName();
            case 2:
                return elm.getTypeString();
            case 3:
                return elm.getParam();
        }
        return "";
    }

    public void changeData(ArrayList<ResponseOut> outputParams) {
        this.data = outputParams;
        fireTableDataChanged();
    }

    public ResponseOut getItem(int selectedRow) {
        return data.get(selectedRow);
    }
}
