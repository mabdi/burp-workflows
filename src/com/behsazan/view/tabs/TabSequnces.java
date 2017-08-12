package com.behsazan.view.tabs;

import burp.BurpExtender;
import com.behsazan.model.adapters.TableModelSequences;
import com.behsazan.model.sqlite.SqliteHelper;
import com.behsazan.view.abstracts.AbstractTab;
import com.behsazan.view.dialogs.DialogSequenceEdit;
import com.behsazan.view.dialogs.DialogSequenceNew;
import com.behsazan.view.dialogs.DialogSequencePlay;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by admin on 07/29/2017.
 */
public class TabSequnces extends AbstractTab {

    private JPanel toolbar;
    private JTable table;
    private Component tableScroll;
    private TableModelSequences tableModel;

    @Override
    protected void initUI() {
        setLayout(new BorderLayout());
        add(getToolbar(), BorderLayout.NORTH);
        add(getTable(), BorderLayout.CENTER);

        table.getColumnModel().getColumn(0).setPreferredWidth(10);
        table.getColumnModel().getColumn(1).setPreferredWidth(400);
        table.getColumnModel().getColumn(2).setPreferredWidth(40);
        table.getColumnModel().getColumn(3).setPreferredWidth(400);
        table.getColumnModel().getColumn(4).setPreferredWidth(400);
    }

    @Override
    public String getTabTitle() {
        return "Sequences";
    }

    public Component getToolbar() {
        if(toolbar == null){
            toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton newNoCookie = new JButton("New");
            newNoCookie.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    BurpExtender.getInstance().getCallbacks().setProxyInterceptionEnabled(false);
                    DialogSequenceNew dlg = new DialogSequenceNew(TabSequnces.this);
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
                        JOptionPane.showMessageDialog(TabSequnces.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
                    DialogSequenceEdit dlg = new DialogSequenceEdit(TabSequnces.this,id);
                    dlg.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            refreshMainView();
                        }
                    });
                }
            });
            JButton clone = new JButton("Clone");
            clone.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int tableSelectedRow = table.getSelectedRow();
                    if(tableSelectedRow<0){
                        JOptionPane.showMessageDialog(TabSequnces.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
                    String name = (String) tableModel.getValueAt(tableSelectedRow,1);
                    String response = JOptionPane.showInputDialog(TabSequnces.this,"Enter new sequence Name: ","Copy Of " + name);
                    if(!response.isEmpty()){
                        SqliteHelper db = new SqliteHelper();
                        if(db.isSequenceNameUsed(response)){
                            JOptionPane.showMessageDialog(TabSequnces.this,"The name is duplicated.","Error",JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        db.cloneSequence(id,response);
                        refreshMainView();
                    }
                }
            });
            JButton delete = new JButton("Delete");
            delete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int tableSelectedRow = table.getSelectedRow();
                    if(tableSelectedRow<0){
                        JOptionPane.showMessageDialog(TabSequnces.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
                    SqliteHelper db = new SqliteHelper();
                    if(!db.isPossibleToDeleteSequence(id)){
                        JOptionPane.showMessageDialog(TabSequnces.this,"The Sequence is used somewhere.","Oops!",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    int response = JOptionPane.showConfirmDialog(TabSequnces.this,"Are you sure to delete sequence with Id="+id,"Delete",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(response == JOptionPane.YES_OPTION){
                        db.deleteSequence(id);
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
                        JOptionPane.showMessageDialog(TabSequnces.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
                    DialogSequencePlay dlg = new DialogSequencePlay(TabSequnces.this,id);

                }
            });
            toolbar.add(newNoCookie);
            toolbar.add(editView);
            toolbar.add(clone);
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
            tableModel = new TableModelSequences();
            table.setModel(tableModel);
            tableScroll = new JScrollPane(table);
        }
        return tableScroll;
    }

}
