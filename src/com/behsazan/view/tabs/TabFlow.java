package com.behsazan.view.tabs;

import com.behsazan.model.DataUtils;
import com.behsazan.model.adapters.TableModelFlows;
import com.behsazan.model.entity.Flow;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractTab;
import com.behsazan.view.dialogs.DialogFlowEdit;
import com.behsazan.view.dialogs.DialogFlowNew;
import com.behsazan.view.dialogs.DialogFlowPlay;
import com.behsazan.view.dialogs.DialogWaiting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.SQLException;

/**
 * Created by admin on 08/02/2017.
 */
public class TabFlow extends AbstractTab {

    private JPanel toolbar;
    private JTable table;
    private Component tableScroll;
    private TableModelFlows tableModel;
    private JPopupMenu popup;

    @Override
    protected void initUI() {
        setLayout(new BorderLayout());
        add(getToolbar(), BorderLayout.NORTH);
        add(getTable(), BorderLayout.CENTER);

        table.getColumnModel().getColumn(0).setMinWidth(10);
        table.getColumnModel().getColumn(1).setMinWidth(400);
        table.getColumnModel().getColumn(2).setMinWidth(40);
        table.getColumnModel().getColumn(3).setMinWidth(40);
    }

    @Override
    public String getTabTitle() {
        return "Flows";
    }

    public Component getToolbar() {
        if(toolbar == null){
            toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton newBtn = new JButton("New");
            newBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DialogFlowNew dlg = new DialogFlowNew();
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
            JButton playTest = new JButton("Play/Test");
            playTest.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionPlay();

                }
            });
            JButton export = new JButton("Export");
            export.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionExport();
                }
            });
            toolbar.add(newBtn);
            toolbar.add(editView);
            toolbar.add(clone);
            toolbar.add(delete);
            toolbar.add(playTest);
            toolbar.add(export);
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
            tableModel = new TableModelFlows();
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
            JMenuItem menuItemPlay = new JMenuItem("Test");
            menuItemPlay.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionPlay();
                }
            });
            JMenuItem menuItemExport = new JMenuItem("Export");
            menuItemExport.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionExport();
                }
            });
            popup.add(menuItemEdit);
            popup.add(menuItemClone);
            popup.add(menuItemDelete);
            popup.add(menuItemPlay);
            popup.add(menuItemExport);
        }
        return  popup;
    }

    private void actionExport() {
        int tableSelectedRow = table.getSelectedRow();
        if(tableSelectedRow<0){
            JOptionPane.showMessageDialog(TabFlow.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
            return;
        }
        final int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);

        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(TabFlow.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File file = fc.getSelectedFile();
            final DialogWaiting pleaseWaitDialog = DialogWaiting.showWaitingDialog(TabFlow.this);
            final SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {

                @Override
                protected Void doInBackground() throws Exception {
                    DataUtils.exportFlow(id,file);
                    return null;
                }

                @Override
                protected void done() {
                    pleaseWaitDialog.dispose();
                    refreshMainView();
                }
            };
            worker.execute();
            pleaseWaitDialog.setVisible(true);
        }
    }

    private void actionPlay() {
        int tableSelectedRow = table.getSelectedRow();
        if(tableSelectedRow<0){
            JOptionPane.showMessageDialog(TabFlow.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
        DialogFlowPlay dlg = new DialogFlowPlay( );
        dlg.setData(id);
    }

    private void actionDelete() {
        int tableSelectedRow = table.getSelectedRow();
        if(tableSelectedRow<0){
            JOptionPane.showMessageDialog(TabFlow.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
            return;
        }
        final int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
        int response = JOptionPane.showConfirmDialog(TabFlow.this,"Are you sure to delete Flow with Id="+id,"Delete",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
        if(response == JOptionPane.YES_OPTION){
            final DialogWaiting pleaseWaitDialog = DialogWaiting.showWaitingDialog(TabFlow.this);
            final SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {

                @Override
                protected Void doInBackground() throws SQLException {
                    Flow.deleteFlow(id);
                    return null;
                }

                @Override
                protected void done() {
                    pleaseWaitDialog.dispose();
                    refreshMainView();
                }
            };
            worker.execute();
            pleaseWaitDialog.setVisible(true);
        }
    }

    private void actionClone() {
        int tableSelectedRow = table.getSelectedRow();
        if(tableSelectedRow<0){
            JOptionPane.showMessageDialog(TabFlow.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
        String name = (String) tableModel.getValueAt(tableSelectedRow,1);
        String response = JOptionPane.showInputDialog(TabFlow.this,"Enter new Flow Name: ","Copy Of " + name);
        if(response!=null && !response.isEmpty()){
            if(Flow.isFlowNameUsed(response)){
                JOptionPane.showMessageDialog(TabFlow.this,"The name is duplicated.","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Flow.cloneFlow(id,response);
                refreshMainView();
            } catch (SQLException e1) {
                e1.printStackTrace();
                UIUtils.showGenerealError( );
            }

        }
    }

    private void actionEdit() {
        int tableSelectedRow = table.getSelectedRow();
        if(tableSelectedRow<0){
            JOptionPane.showMessageDialog(TabFlow.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
        DialogFlowEdit dlg = new DialogFlowEdit();
        dlg.initData(id);
        dlg.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                refreshMainView();
            }
        });
    }
}
