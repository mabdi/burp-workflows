package com.behsazan.model.adapters;

import com.behsazan.model.entity.RequestIn;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 08/08/2017.
 */
public class TableModelRequestIn extends AbstractTableModel {
    public static final String[] COLUMNS = new String[]{
            "Id","Name","Type"
    };
    private List<RequestIn> data;

    public TableModelRequestIn() {
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
        RequestIn elm = data.get(rowIndex);
        switch (columnIndex){
            case 0:
                return elm.getId();
            case 1:
                return elm.getPlaceHoder();
            case 2:
                return elm.getTypeString();
        }
        return "";
    }

    public void changeData(List<RequestIn> inputParams) {
        this.data = inputParams;
        fireTableDataChanged();
    }

    public RequestIn getItem(int selectedRow) {
        return data.get(selectedRow);
    }
}
