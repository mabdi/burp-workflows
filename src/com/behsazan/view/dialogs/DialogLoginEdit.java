package com.behsazan.view.dialogs;

import burp.BurpExtender;
import com.behsazan.model.entity.Login;
import com.behsazan.model.entity.Flow;
import com.behsazan.model.settings.Settings;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;

import static com.behsazan.view.UIUtils.FormUtility;

/**
 * Created by admin on 08/21/2017.
 */
@Deprecated
public class DialogLoginEdit extends AbstractDialog {

    private JPanel formPanel;
    private JPanel buttonsPanel;
    private JTextField txtUsername;
    private JTextField txtPassword;
    private JTextField txtParam;
    private JComboBox<String> cmbFlow;
    private DefaultComboBoxModel<String> modelCombo;
    private Login loging;
    private JComboBox<String> cmbUrls;

    public DialogLoginEdit() {
        super(false);
    }

    @Override
    protected void initUI() {
        setSize(400, 300);
        setTitle("Edit Login");
        setLocationRelativeTo(getParentWindow());
        installEscapeCloseOperation();
        setLayout(new BorderLayout());
        add(getFormPanel(), BorderLayout.CENTER);
        add(getButtonsPanel(), BorderLayout.SOUTH);
    }

    public void setData(int id){
        Login login = Login.getById(id);
        this.loging = login;
        cmbUrls.setSelectedItem(login.getUrl());
        txtUsername.setText(login.getUsername());
        txtParam.setText(login.getOutParam());
        txtPassword.setText(login.getPassword());
        cmbFlow.setSelectedItem(login.getFlow().getName());
        setVisible(true);
    }

    public JPanel getFormPanel() {
        if(formPanel == null){
            formPanel = new JPanel();
            formPanel.setLayout(new GridBagLayout());
            FormUtility formUtility = new FormUtility(formPanel);

            formUtility.addLabel("Url :");
            DefaultComboBoxModel<String> urls = new DefaultComboBoxModel<String>();
            for(String u: Settings.getBaseUrls()){
                urls.addElement(u);
            }
            cmbUrls = new JComboBox<>(urls);
            formUtility.addLastField(cmbUrls);

            formUtility.addLabel("Out Param Name:");
            txtParam = new JTextField();
            txtParam.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());
            formUtility.addLastField(txtParam);

            formUtility.addLabel("Username :");
            txtUsername = new JTextField();
            txtUsername.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());
            formUtility.addLastField(txtUsername);

            formUtility.addLabel("Password :");
            txtPassword = new JTextField();
            txtPassword.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());
            formUtility.addLastField(txtPassword);

            formUtility.addLabel("Flow :");
            modelCombo = new DefaultComboBoxModel<>();
            cmbFlow = new JComboBox<>(modelCombo);
            List<String> tests = Flow.getAllFlowName();
            for (String t: tests) {
                modelCombo.addElement(t);
            }
            formUtility.addLastField(cmbFlow);
        }
        return formPanel;
    }

    public JPanel getButtonsPanel() {
        if(buttonsPanel==null){
            buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dissmiss();
                }
            });
            JButton addBtn = new JButton("Save");
            addBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String username = txtUsername.getText();
                    String password = txtPassword.getText();
                    String param = txtParam.getText();
                    String url = (String) cmbUrls.getSelectedItem();
                    String selectedFlow = (String) cmbFlow.getSelectedItem();
                    Flow flow = Flow.getByName(selectedFlow);
                    if(username.isEmpty() || password.isEmpty() || param.isEmpty() || url.isEmpty()){
                        JOptionPane.showMessageDialog(DialogLoginEdit.this,"Some required filed is not set.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try{
                        new URL(url);
                    }catch (Exception x) {
                        JOptionPane.showMessageDialog(DialogLoginEdit.this,"Invalid URL.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        loging.setUrl(url);
                        loging.setUsername(username);
                        loging.setPassword(password);
                        loging.setOutParam(param);
                        loging.setFlow(flow);
                        Login.updateLogin(loging);
                        dissmiss();
                    }catch (Exception x){
                        BurpExtender.getInstance().getStdout().println("save Error "+x.getMessage() + "\n");
                        x.printStackTrace(BurpExtender.getInstance().getStdout());
                    }
                }
            });
            buttonsPanel.add(cancelBtn);
            buttonsPanel.add(addBtn);
        }
        return buttonsPanel;
    }
}