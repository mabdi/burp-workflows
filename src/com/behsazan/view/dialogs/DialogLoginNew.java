package com.behsazan.view.dialogs;

import burp.BurpExtender;
import com.behsazan.model.entity.Login;
import com.behsazan.model.entity.Flow;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;

/**
 * Created by admin on 08/21/2017.
 */
public class DialogLoginNew extends AbstractDialog {

    private JPanel formPanel;
    private JPanel buttonsPanel;
    private JTextField txtUsername;
    private JTextField txtPassword;
    private JTextField txtParam;
    private JComboBox<String> cmbFlow;
    private DefaultComboBoxModel<String> modelCombo;
    private JTextField txtUrl;
    private JTextField txtBase;

    public DialogLoginNew() {
        super();
    }

    @Override
    protected void initUI() {
        setSize(400, 300);
        setTitle("New Login");
        setLocationRelativeTo(getParentWindow());
        setLayout(new BorderLayout());
        add(getFormPanel(), BorderLayout.CENTER);
        add(getButtonsPanel(), BorderLayout.SOUTH);
    }

    public JPanel getFormPanel() {
        if(formPanel == null){
            formPanel = new JPanel();
            formPanel.setLayout(new GridBagLayout());
            UIUtils.FormUtility formUtility = new UIUtils.FormUtility();

            formUtility.addLabel("Url :", formPanel);
            txtUrl = new JTextField();
            formUtility.addLastField(txtUrl, formPanel);

            formUtility.addLabel("Base :", formPanel);
            txtBase = new JTextField();
            formUtility.addLastField(txtBase, formPanel);

            formUtility.addLabel("Username :", formPanel);
            txtUsername = new JTextField();
            txtUsername.setToolTipText("Local variable( @@username@@ )");
            formUtility.addLastField(txtUsername, formPanel);

            formUtility.addLabel("Password :", formPanel);
            txtPassword = new JTextField();
            txtPassword.setToolTipText("Local variable( @@password@@ )");
            formUtility.addLastField(txtPassword, formPanel);

            formUtility.addLabel("Out Param Name:", formPanel);
            txtParam = new JTextField();
            formUtility.addLastField(txtParam, formPanel);
            formUtility.addLabel("Flow :", formPanel);
            modelCombo = new DefaultComboBoxModel<>();
            cmbFlow = new JComboBox<>(modelCombo);
            List<String> tests = Flow.getAllFlowName();
            for (String t: tests) {
                modelCombo.addElement(t);
            }
            formUtility.addLastField(cmbFlow, formPanel);
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
                    String url = txtUrl.getText();
                    String base = txtBase.getText();
                    String selectedFlow = (String) cmbFlow.getSelectedItem();
                    Flow flow = Flow.getByName(selectedFlow);
                    if(username.isEmpty() || password.isEmpty() || param.isEmpty() || url.isEmpty() || base.isEmpty()){
                        JOptionPane.showMessageDialog(DialogLoginNew.this,"Some required filed is not set.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try{
                        new URL(url);
                    }catch (Exception x) {
                        JOptionPane.showMessageDialog(DialogLoginNew.this,"Invalid URL.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        Login.insertLogin(new Login(-1,username,password,param,url,base,"",-1,flow));
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