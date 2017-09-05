package com.behsazan.view.tabs;

import com.behsazan.model.adapters.TableModelLogins;
import com.behsazan.model.entity.Login;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractTab;
import com.behsazan.view.dialogs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

/**
 * Created by admin on 07/29/2017.
 */
public class TabLogins extends AbstractTab {

    private JPanel toolbar;
    private JTable table;
    private Component tableScroll;
    private TableModelLogins tableModel;
    private JPopupMenu popup;

    @Override
    protected void initUI() {

        setLayout(new BorderLayout());
        add(getToolbar(), BorderLayout.NORTH);
        add(getTable(), BorderLayout.CENTER);

        table.getColumnModel().getColumn(0).setMinWidth(10);
        table.getColumnModel().getColumn(1).setMinWidth(100);
        table.getColumnModel().getColumn(2).setMinWidth(100);
        table.getColumnModel().getColumn(3).setMinWidth(100);
        table.getColumnModel().getColumn(4).setMinWidth(100);
        table.getColumnModel().getColumn(5).setMinWidth(100);
    }

    @Override
    public String getTabTitle() {
        return "Logins";
    }

    public Component getToolbar() {
        if(toolbar == null){
            toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton newbtn = new JButton("New");
            newbtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DialogLoginNew dlg = new DialogLoginNew();
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
            toolbar.add(newbtn);
            toolbar.add(editView);
            toolbar.add(delete);
            toolbar.add(clone);
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
            tableModel = new TableModelLogins();
            table.setModel(tableModel);
            tableScroll = new JScrollPane(table);
            table.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                }

                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        JTable source = (JTable) e.getSource();
                        int row = source.rowAtPoint(e.getPoint());
                        int column = source.columnAtPoint(e.getPoint());

                        if (!source.isRowSelected(row))
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
            popup.add(menuItemEdit);
            popup.add(menuItemClone);
            popup.add(menuItemDelete);
            popup.add(menuItemPlay);
        }
        return  popup;
    }

    private void actionClone() {
        int tableSelectedRow = table.getSelectedRow();
        if(tableSelectedRow<0){
            JOptionPane.showMessageDialog(TabLogins.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);

        try {
            Login.cloneLogin(id);
            refreshMainView();
        } catch (SQLException e1) {
            e1.printStackTrace();
            UIUtils.showGenerealError( );
        }
    }

    private void actionDelete() {
        int tableSelectedRow = table.getSelectedRow();
        if(tableSelectedRow<0){
            JOptionPane.showMessageDialog(TabLogins.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
            return;
        }
        final int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
        int response = JOptionPane.showConfirmDialog(TabLogins.this,"Are you sure to delete Login with Id="+id,"Delete",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
        if(response == JOptionPane.YES_OPTION){
            final DialogWaiting pleaseWaitDialog = DialogWaiting.showWaitingDialog(TabLogins.this);
            final SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {

                @Override
                protected Void doInBackground() throws SQLException {
                    Login.deleteLogin(id);
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
            JOptionPane.showMessageDialog(TabLogins.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
        DialogLoginPlay dlg = new DialogLoginPlay();
        dlg.setData(id);
    }

    private void actionEdit() {
        int tableSelectedRow = table.getSelectedRow();
        if(tableSelectedRow<0){
            JOptionPane.showMessageDialog(TabLogins.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
        DialogLoginEdit dlg = new DialogLoginEdit();
        dlg.setData(id);
        dlg.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                refreshMainView();
            }
        });
    }
}
