package com.behsazan.view.tabs;

import burp.BurpExtender;
import com.behsazan.model.adapters.TableModelSequences;
import com.behsazan.model.entity.Sequence;
import com.behsazan.model.sqlite.SqliteHelper;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractTab;
import com.behsazan.view.dialogs.DialogSequenceEdit;
import com.behsazan.view.dialogs.DialogSequenceNew;
import com.behsazan.view.dialogs.DialogSequencePlay;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

/**
 * Created by admin on 07/29/2017.
 */
public class TabSequnces extends AbstractTab {

    private JPanel toolbar;
    private JTable table;
    private Component tableScroll;
    private TableModelSequences tableModel;
    private JPopupMenu popup;

    @Override
    protected void initUI() {
        setLayout(new BorderLayout());
        add(getToolbar(), BorderLayout.NORTH);
        add(getTable(), BorderLayout.CENTER);

        table.getColumnModel().getColumn(0).setMinWidth(20);
        table.getColumnModel().getColumn(1).setMinWidth(100);
        table.getColumnModel().getColumn(2).setMinWidth(40);
        table.getColumnModel().getColumn(3).setMinWidth(100);
        table.getColumnModel().getColumn(4).setMinWidth(100);
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
                    actionNew();
                }
            });
            JButton editView = new JButton("Edit/View");
            editView.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionEdit();
                }
            });
            JButton clone = new JButton("Clone");
            clone.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionClone();
                }
            });
            JButton delete = new JButton("Delete");
            delete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionDelete();
                }
            });
//            JButton playTest = new JButton("Play/Test");
//            playTest.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    int tableSelectedRow = table.getSelectedRow();
//                    if(tableSelectedRow<0){
//                        JOptionPane.showMessageDialog(TabSequnces.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
//                        return;
//                    }
//
//                    int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
//                    DialogSequencePlay dlg = new DialogSequencePlay(id);
//
//                }
//            });
            toolbar.add(newNoCookie);
            toolbar.add(editView);
            toolbar.add(clone);
            toolbar.add(delete);
//            toolbar.add(playTest);
        }
        return toolbar;
    }

    private void actionDelete() {
        int tableSelectedRow = table.getSelectedRow();
        if(tableSelectedRow<0){
            JOptionPane.showMessageDialog(TabSequnces.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
        if(!Sequence.isPossibleToDeleteSequence(id)){
            JOptionPane.showMessageDialog(TabSequnces.this,"The Sequence is used somewhere.","Oops!",JOptionPane.ERROR_MESSAGE);
            return;
        }
        int response = JOptionPane.showConfirmDialog(TabSequnces.this,"Are you sure to delete sequence with Id="+id,"Delete",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
        if(response == JOptionPane.YES_OPTION){
            try {
                Sequence.deleteSequence(id);
                refreshMainView();
            } catch (SQLException e1) {
                e1.printStackTrace();
                UIUtils.showGenerealError( );
            }
        }
    }

    private void actionClone() {
        int tableSelectedRow = table.getSelectedRow();
        if(tableSelectedRow<0){
            JOptionPane.showMessageDialog(TabSequnces.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
        String name = (String) tableModel.getValueAt(tableSelectedRow,1);
        String response = JOptionPane.showInputDialog(TabSequnces.this,"Enter new sequence Name: ","Copy Of " + name);
        if(response!= null && !response.isEmpty()){
            if(Sequence.isSequenceNameUsed(response)){
                JOptionPane.showMessageDialog(TabSequnces.this,"The name is duplicated.","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Sequence.cloneSequence(id,response);
            } catch (SQLException e1) {
                e1.printStackTrace();
                UIUtils.showGenerealError();
            }
            refreshMainView();
        }
    }

    private void actionEdit() {
        int tableSelectedRow = table.getSelectedRow();
        if(tableSelectedRow<0){
            JOptionPane.showMessageDialog(TabSequnces.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
        DialogSequenceEdit dlg = new DialogSequenceEdit(id);
        dlg.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                refreshMainView();
            }
        });
    }

    private void actionNew() {
        BurpExtender.getInstance().getCallbacks().setProxyInterceptionEnabled(false);
        DialogSequenceNew dlg = new DialogSequenceNew();
        dlg.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                refreshMainView();
            }
        });
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
            table.addMouseListener( new MouseAdapter()
            {
                public void mousePressed(MouseEvent e)
                {
                }

                public void mouseReleased(MouseEvent e)
                {
                    if (e.isPopupTrigger())
                    {
                        JTable source = (JTable)e.getSource();
                        int row = source.rowAtPoint( e.getPoint() );
                        int column = source.columnAtPoint( e.getPoint() );

                        if (! source.isRowSelected(row))
                            source.changeSelection(row, column, false, false);

                        getJtablePopup().show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
        }
        return tableScroll;
    }

    private JPopupMenu getJtablePopup() {
        if(popup == null) {
            popup = new JPopupMenu();
            JMenuItem menuItemEdit = new JMenuItem("Edit");
            menuItemEdit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionEdit();
                }
            });
            JMenuItem menuItemClone = new JMenuItem("Clone");
            menuItemClone.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionClone();
                }
            });
            JMenuItem menuItemDelete = new JMenuItem("Delete");
            menuItemDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionDelete();
                }
            });
            popup.add(menuItemEdit);
            popup.add(menuItemClone);
            popup.add(menuItemDelete);
        }
        return  popup;
    }

}
