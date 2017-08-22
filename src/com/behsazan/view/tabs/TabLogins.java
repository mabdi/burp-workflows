package com.behsazan.view.tabs;

import com.behsazan.model.adapters.TableModelLogins;
import com.behsazan.model.adapters.TableModelTestCases;
import com.behsazan.model.entity.Login;
import com.behsazan.model.sqlite.SqliteHelper;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractTab;
import com.behsazan.view.dialogs.*;
import sun.rmi.runtime.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

/**
 * Created by admin on 07/29/2017.
 */
public class TabLogins extends AbstractTab {



    private JPanel toolbar;
    private JTable table;
    private Component tableScroll;
    private TableModelLogins tableModel;


    @Override
    protected void initUI() {

        setLayout(new BorderLayout());
        add(getToolbar(), BorderLayout.NORTH);
        add(getTable(), BorderLayout.CENTER);

        table.getColumnModel().getColumn(0).setPreferredWidth(10);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
    }

    @Override
    public String getTabTitle() {
        return "Logins";
    }

    public Component getToolbar() {
        if(toolbar == null){
            toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton newTestCase = new JButton("New");
            newTestCase.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DialogLoginNew dlg = new DialogLoginNew(TabLogins.this);
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
                        JOptionPane.showMessageDialog(TabLogins.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
                    DialogLoginEdit dlg = new DialogLoginEdit(TabLogins.this);
                    dlg.setData(id);
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
                        JOptionPane.showMessageDialog(TabLogins.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);

                    try {
                        Login.cloneLogin(id);
                        refreshMainView();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                        UIUtils.showGenerealError(TabLogins.this);
                    }


                }
            });
            JButton delete = new JButton("Delete");
            delete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
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
            });
            JButton playTest = new JButton("Play/Test");
            playTest.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int tableSelectedRow = table.getSelectedRow();
                    if(tableSelectedRow<0){
                        JOptionPane.showMessageDialog(TabLogins.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
                    DialogLoginPlay dlg = new DialogLoginPlay(TabLogins.this);
                    dlg.setData(id);

                }
            });
            toolbar.add(newTestCase);
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
        }
        return tableScroll;
    }
}
