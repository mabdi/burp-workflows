package com.behsazan.view.tabs;

import burp.BurpExtender;
import com.behsazan.model.adapters.TableModelTestCases;
import com.behsazan.model.sqlite.SqliteHelper;
import com.behsazan.view.abstracts.AbstractTab;
import com.behsazan.view.dialogs.DialogTestCaseEdit;
import com.behsazan.view.dialogs.DialogTestCaseNew;
import com.behsazan.view.dialogs.DialogTestCasePlay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by admin on 08/02/2017.
 */
public class TabTestCases extends AbstractTab {

    private JPanel toolbar;
    private JTable table;
    private Component tableScroll;
    private TableModelTestCases tableModel;

    @Override
    protected void initUI() {
        setLayout(new BorderLayout());
        add(getToolbar(), BorderLayout.NORTH);
        add(getTable(), BorderLayout.CENTER);

        table.getColumnModel().getColumn(0).setPreferredWidth(10);
        table.getColumnModel().getColumn(1).setPreferredWidth(400);
        table.getColumnModel().getColumn(2).setPreferredWidth(40);
        table.getColumnModel().getColumn(3).setPreferredWidth(40);
    }

    @Override
    public String getTabTitle() {
        return "Test Cases";
    }

    public Component getToolbar() {
        if(toolbar == null){
            toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton newTestCase = new JButton("New");
            newTestCase.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DialogTestCaseNew dlg = new DialogTestCaseNew(TabTestCases.this);
                    dlg.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            refreshMainView();
                        }
                    });
                }
            });
            JButton editView = new JButton("Edit/View");
            editView.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int tableSelectedRow = table.getSelectedRow();
                    if(tableSelectedRow<0){
                        JOptionPane.showMessageDialog(TabTestCases.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
                    DialogTestCaseEdit dlg = new DialogTestCaseEdit(TabTestCases.this);
                    dlg.initData(id);
                    dlg.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            refreshMainView();
                        }
                    });
                }
            });
            JButton delete = new JButton("Delete");
            delete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int tableSelectedRow = table.getSelectedRow();
                    if(tableSelectedRow<0){
                        JOptionPane.showMessageDialog(TabTestCases.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
                    int response = JOptionPane.showConfirmDialog(TabTestCases.this,"Are you sure to delete sequence with Id="+id,"Delete",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(response == JOptionPane.YES_OPTION){
                        new SqliteHelper().deleteTestCase(id);
                        refreshMainView();
                    }
                }
            });
            JButton playTest = new JButton("Play/Test");
            playTest.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int tableSelectedRow = table.getSelectedRow();
                    if(tableSelectedRow<0){
                        JOptionPane.showMessageDialog(TabTestCases.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
                    DialogTestCasePlay dlg = new DialogTestCasePlay(TabTestCases.this);
                    dlg.setData(id);

                }
            });
            toolbar.add(newTestCase);
            toolbar.add(editView);
            toolbar.add(delete);
            toolbar.add(playTest);
        }
        return toolbar;
    }

    private void refreshMainView() {
        tableModel.updateData();
        tableModel.fireTableDataChanged();
    }

    public Component getTable() {
        if(tableScroll == null){
            table = new JTable();
            tableModel = new TableModelTestCases();
            table.setModel(tableModel);
            tableScroll = new JScrollPane(table);
        }
        return tableScroll;
    }
}
