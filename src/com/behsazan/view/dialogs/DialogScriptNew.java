package com.behsazan.view.dialogs;

import burp.BurpExtender;
import com.behsazan.model.DataUtils;
import com.behsazan.model.entity.Script;
import com.behsazan.model.settings.Settings;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractDialog;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Created by admin on 09/05/2017.
 */
public class DialogScriptNew extends AbstractDialog {


    private JPanel topPanel;
    private JTextField txtName;
    private RTextScrollPane centerCodeEditor;
    private JPanel buttonsPanel;
    private JComboBox<String> comboType;
    private JComboBox<String> comboLang;
    private RSyntaxTextArea textArea;
    private String codeTemplate;


    @Override
    protected void initUI() {
        setSize(800, 600);
        setTitle("New Script");
        setLocationRelativeTo(getParentWindow());
        installEscapeCloseOperation();
        setLayout(new BorderLayout());
        add(getTopPanel(), BorderLayout.NORTH);
        add(getCenterCodeEditor(), BorderLayout.CENTER);
        add(getButtonsPanel(), BorderLayout.SOUTH);

        comboLang.setSelectedIndex(0);
        comboType.setSelectedIndex(0);

    }

    public JPanel getTopPanel() {
        if (topPanel == null) {
            topPanel = new JPanel(new GridBagLayout());
            UIUtils.FormUtility form = new UIUtils.FormUtility(topPanel);
            form.addLabel("Name:");
            txtName = new JTextField();
            form.addLastField(txtName);

            form.addLabel("Type:");
            comboType = new JComboBox<>();
            DefaultComboBoxModel<String> modelCombo = new DefaultComboBoxModel<>();
            Map<Integer, String> kvp = Script.getTypesString();
            for (String v : kvp.values()) {
                modelCombo.addElement(v);
            }
            comboType.setModel(modelCombo);
            comboType.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    alertChange();
                }
            });
            form.addLastField(comboType);

            form.addLabel("Language:");
            comboLang = new JComboBox<>();
            DefaultComboBoxModel<String> modelCombo2 = new DefaultComboBoxModel<>();
            kvp = Script.getLangsString();
            for (String v : kvp.values()) {
                modelCombo2.addElement(v);
            }
            comboLang.setModel(modelCombo2);
            comboLang.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    alertChange();
                }
            });
            form.addLastField(comboLang);
        }
        return topPanel;
    }

    private void alertChange() {
        if (codeTemplate != null && !textArea.getText().equals(codeTemplate)) {
            int rsp = JOptionPane.showConfirmDialog(DialogScriptNew.this, "Modified text will be lost. Are you sure to change?", "Change Data", JOptionPane.YES_NO_OPTION);
            if (rsp == JOptionPane.YES_OPTION) {
                updateEditor();
            }
        }else{
            updateEditor();
        }
    }

    private void updateEditor() {
        String ext = ".js";

        switch (getSelectedLang()) {
            case Script.LANG_JS:
                textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
                textArea.setEditable(true);
                ext = ".js";
                break;
            case Script.LANG_PYTHON:
                textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
                textArea.setEditable(true);
                ext = ".py";
                break;
            case Script.LANG_RUBY:
                textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_RUBY);
                textArea.setEditable(true);
                ext = ".rb";
                break;
        }
        try {
            switch (getSelectedType()) {
                case Script.TYPE_ON_TEST_START:
                    codeTemplate = DataUtils.readAsset(Settings.FILENAME_ON_TEST_START + ext);
                    break;
                case Script.TYPE_ON_TEST_FINISH:
                    codeTemplate = DataUtils.readAsset(Settings.FILENAME_ON_TEST_FINISH + ext);
                    break;
                case Script.TYPE_ON_SEQUENCE_START:
                    codeTemplate = DataUtils.readAsset(Settings.FILENAME_ON_SEQUENCE_START+ ext);
                    break;
                case Script.TYPE_ON_SEQUENCE_FINISH:
                    codeTemplate = DataUtils.readAsset(Settings.FILENAME_ON_SEQUENCE_FINISH+ ext);
                    break;
                case Script.TYPE_ON_REQUEST_BEFORE_ASSIGNMENT:
                    codeTemplate = DataUtils.readAsset(Settings.FILENAME_ON_REQUEST_BEFORE_ASSIGNMENT + ext);
                    break;
                case Script.TYPE_ON_REQUEST_AFTER_ASSIGNMENT:
                    codeTemplate = DataUtils.readAsset(Settings.FILENAME_ON_REQUEST_AFTER_ASSIGNMENT + ext);
                    break;
                case Script.TYPE_ON_RESPONSE_RECEIVED:
                    codeTemplate = DataUtils.readAsset(Settings.FILENAME_ON_RESPONSE_RECEIVED + ext);
                    break;
            }

            textArea.setText(codeTemplate);
        } catch (IOException ex) {
            ex.printStackTrace(BurpExtender.getInstance().getStdout());
        }
        textArea.revalidate();
    }

    public int getSelectedType() {
        String selected = (String) comboType.getSelectedItem();
        Set<Map.Entry<Integer, String>> kvp = Script.getTypesString().entrySet();
        for (Map.Entry<Integer, String> kv : kvp) {
            if (kv.getValue().equals(selected)) {
                return kv.getKey();
            }
        }
        return Script.TYPE_ON_TEST_START;
    }

    public int getSelectedLang() {
        String selected = (String) comboLang.getSelectedItem();
        Set<Map.Entry<Integer, String>> kvp = Script.getLangsString().entrySet();
        for (Map.Entry<Integer, String> kv : kvp) {
            if (kv.getValue().equals(selected)) {
                return kv.getKey();
            }
        }
        return Script.LANG_JS;
    }

    public RTextScrollPane getCenterCodeEditor() {
        if (centerCodeEditor == null) {
            textArea = new RSyntaxTextArea(20, 60);
            textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
            textArea.setCodeFoldingEnabled(true);
            textArea.setEditable(true);
            centerCodeEditor = new RTextScrollPane(textArea);
        }
        return centerCodeEditor;
    }

    public JPanel getButtonsPanel() {
        if (buttonsPanel == null) {
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
                    String name = txtName.getText();
                    String script = textArea.getText();
                    int type = getSelectedType();
                    int lang = getSelectedLang();
                    if(name.isEmpty() ){
                        JOptionPane.showMessageDialog(DialogScriptNew.this,"Some required filed is not set.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try {
                        Script.insert(new Script(-1, name, script, type,lang));
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
