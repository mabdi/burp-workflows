package com.behsazan.view.tabs;

import com.behsazan.model.DataUtils;
import com.behsazan.model.settings.Settings;
import com.behsazan.model.sqlite.SettingDb;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractTab;
import com.behsazan.view.dialogs.DialogWaiting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;

/**
 * Created by admin on 07/29/2017.
 */
public class TabSettings extends AbstractTab {


    private JButton backupButton;
    private JPanel toolbar;
    private JButton export;
    private JButton btnImport;
    private JPanel body;
    private JTextField txtDelay;
    private JTextField txtSession;
    private JTextArea txtUrls;
    private JButton updateButton;
    private JTextArea txtFilters;

    @Override
    public String getTabTitle() {
        return "Settings";
    }

    @Override
    protected void initUI() {
        setLayout(new BorderLayout());
        add(new JScrollPane(getBody()) , BorderLayout.CENTER);
        add(getToolbar() , BorderLayout.SOUTH);
    }


    public Component getToolbar() {
        if (toolbar == null) {
            toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
            toolbar.add(getUpdateButton());
            toolbar.add(getExportJSONButton());
            toolbar.add(getImportJSONButton());
            toolbar.add(getBackupButton());
        }
        return toolbar;
    }

    public JButton getBackupButton() {
        if(backupButton == null){
            backupButton = new JButton("Backup");
            backupButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            Settings.backupDb(TabSettings.this);
                            return null;
                        }

                        @Override
                        protected void done() {
                            super.done();
                        }
                    }.execute();

                }
            });
        }
        return backupButton;
    }

    public JButton getExportJSONButton(){
        if(export == null) {
            export = new JButton("Export");
            export.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fc = new JFileChooser();
                    int returnVal = fc.showSaveDialog(TabSettings.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        final File file = fc.getSelectedFile();
                        final DialogWaiting pleaseWaitDialog = DialogWaiting.showWaitingDialog(TabSettings.this);
                        final SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {

                            @Override
                            protected Void doInBackground() throws Exception {
                                DataUtils.exportJSON(file);
                                return null;
                            }

                            @Override
                            protected void done() {
                                pleaseWaitDialog.dispose();
                            }
                        };
                        worker.execute();
                        pleaseWaitDialog.setVisible(true);
                    }
                }
            });
        }
        return export;
    }

    private JButton getImportJSONButton() {
        if(btnImport == null){
            btnImport = new JButton("Import");
            btnImport.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fc = new JFileChooser();
                    int returnVal = fc.showOpenDialog(TabSettings.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        final File file = fc.getSelectedFile();
                        final DialogWaiting pleaseWaitDialog = DialogWaiting.showWaitingDialog(TabSettings.this);
                        final SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {

                            @Override
                            protected Void doInBackground() throws Exception {
                                DataUtils.importJSON(file);
                                return null;
                            }

                            @Override
                            protected void done() {
                                pleaseWaitDialog.dispose();
                            }
                        };
                        worker.execute();
                        pleaseWaitDialog.setVisible(true);
                    }
                }
            });
        }
        return btnImport;
    }

    public JPanel getBody() {
        if(body == null){
            body = new JPanel(new GridBagLayout());
            UIUtils.FormUtility form = new UIUtils.FormUtility(body);

            form.addLabel("Delay between requests (millies): ");
            txtDelay = new JTextField(String.valueOf(Settings.getDelay()));
            txtDelay.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());
            form.addLastField(txtDelay);

            form.addLabel("Session cookie name: ");
            txtSession = new JTextField(Settings.getCookie());
            txtSession.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());
            form.addLastField(txtSession);

            form.addLabel("Base URLs: ");
            txtUrls = new JTextArea("",5,10);
            txtUrls.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());
            for(String s: Settings.getBaseUrls()){
                txtUrls.append(s);
                txtUrls.append("\n");
            }
            form.addLastField(txtUrls);

            form.addLabel("Filter patterns: ");
            txtFilters = new JTextArea("",5,10);
            txtFilters.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());
            for(String s: Settings.getFilters()){
                txtFilters.append(s);
                txtFilters.append("\n");
            }
            form.addLastField(txtFilters);
            form.fillReminder();
        }
        return body;
    }

    public JButton getUpdateButton() {
        if(updateButton == null){
            updateButton = new JButton("Update");
            updateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String cookie = txtSession.getText().trim();
                    String delay = txtDelay.getText().trim();
                    String strUrls = txtUrls.getText().trim();
                    String strFilters = txtFilters.getText().trim();
                    if(cookie.isEmpty() || delay.isEmpty() || strUrls.isEmpty()){
                        JOptionPane.showMessageDialog(TabSettings.this,"Some required filed is not set.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try{
                        Long.parseLong(delay);
                    }catch (Exception x){
                        JOptionPane.showMessageDialog(TabSettings.this,"Not valid delay value.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String[] urls = strUrls.split("\n");
                    for(String url:urls){
                        if(!DataUtils.isValidURL(url)){
                            JOptionPane.showMessageDialog(TabSettings.this,"Invalid URL: " + url,"Error",JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    SettingDb db = new SettingDb();
                    try {
                        db.updateKey(Settings.TABLE_KEY_DELAY,delay);
                        db.updateKey(Settings.TABLE_KEY_SESSION,cookie);
                        db.updateKey(Settings.TABLE_KEY_URLS,strUrls);
                        db.updateKey(Settings.TABLE_KEY_FILTER,strFilters);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }

                }
            });
        }
        return updateButton;
    }
}
