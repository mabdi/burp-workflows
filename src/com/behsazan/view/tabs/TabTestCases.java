package com.behsazan.view.tabs;

import burp.BurpExtender;
import com.behsazan.model.DataUtils;
import com.behsazan.model.adapters.TableModelTestCases;
import com.behsazan.model.sqlite.SqliteHelper;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractTab;
import com.behsazan.view.dialogs.DialogTestCaseEdit;
import com.behsazan.view.dialogs.DialogTestCaseNew;
import com.behsazan.view.dialogs.DialogTestCasePlay;
import com.behsazan.view.dialogs.DialogWaiting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

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
            JButton clone = new JButton("Clone");
            clone.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int tableSelectedRow = table.getSelectedRow();
                    if(tableSelectedRow<0){
                        JOptionPane.showMessageDialog(TabTestCases.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
                    String name = (String) tableModel.getValueAt(tableSelectedRow,1);
                    String response = JOptionPane.showInputDialog(TabTestCases.this,"Enter new TestCase Name: ","Copy Of " + name);
                    if(!response.isEmpty()){
                        SqliteHelper db = new SqliteHelper();
                        if(db.isTestCaseNameUsed(response)){
                            JOptionPane.showMessageDialog(TabTestCases.this,"The name is duplicated.","Error",JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        db.cloneTestCase(id,response);
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
                        JOptionPane.showMessageDialog(TabTestCases.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    final int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
                    int response = JOptionPane.showConfirmDialog(TabTestCases.this,"Are you sure to delete TestCase with Id="+id,"Delete",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(response == JOptionPane.YES_OPTION){
                        final DialogWaiting pleaseWaitDialog = DialogWaiting.showWaitingDialog(TabTestCases.this);
                        final SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {

                            @Override
                            protected Void doInBackground() throws Exception {
                                new SqliteHelper().deleteTestCase(id);
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
                        JOptionPane.showMessageDialog(TabTestCases.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);
                    DialogTestCasePlay dlg = new DialogTestCasePlay(TabTestCases.this);
                    dlg.setData(id);

                }
            });
            JButton export = new JButton("Export");
            export.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int tableSelectedRow = table.getSelectedRow();
                    if(tableSelectedRow<0){
                        JOptionPane.showMessageDialog(TabTestCases.this,"No row is selected.","Oops!",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    final int id = (Integer) tableModel.getValueAt(tableSelectedRow,0);

                    JFileChooser fc = new JFileChooser();
                    int returnVal = fc.showSaveDialog(TabTestCases.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        final File file = fc.getSelectedFile();
                        final DialogWaiting pleaseWaitDialog = DialogWaiting.showWaitingDialog(TabTestCases.this);
                        final SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {

                            @Override
                            protected Void doInBackground() throws Exception {
                                DataUtils.exportTestCase(id,file);
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
            toolbar.add(newTestCase);
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
            tableModel = new TableModelTestCases();
            table.setModel(tableModel);
            tableScroll = new JScrollPane(table);
        }
        return tableScroll;
    }
}
