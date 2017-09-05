package com.behsazan.view.dialogs;

import com.behsazan.model.entity.Script;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractDialog;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    }

    public JPanel getTopPanel() {
        if(topPanel == null){
            topPanel = new JPanel(new GridBagLayout());
            UIUtils.FormUtility form = new UIUtils.FormUtility();
            form.addLabel("Name:",topPanel);
            txtName = new JTextField();
            form.addLastField(txtName,topPanel);

            form.addLabel("Type:",topPanel);
            comboType = new JComboBox<>();
            DefaultComboBoxModel<String> modelCombo = new DefaultComboBoxModel<>();
            Map<Integer, String> kvp = Script.getTypesString();
            for (String v:kvp.values() ) {
                modelCombo.addElement(v);
            }
            comboType.setModel(modelCombo);
            form.addLastField(comboType,topPanel);

            form.addLabel("Language:",topPanel);
            comboLang = new JComboBox<>();
            DefaultComboBoxModel<String> modelCombo2 = new DefaultComboBoxModel<>();
            kvp = Script.getLangsString();
            for (String v:kvp.values() ) {
                modelCombo2.addElement(v);
            }
            comboLang.setModel(modelCombo2);
            comboLang.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    switch (getSelectedLang()){
                        case Script.LANG_JS:
                            textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
                            textArea.revalidate();
                            break;
                        case Script.LANG_PYTHON:
                            textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
                            textArea.revalidate();
                            break;
                        case Script.LANG_RUBY:
                            textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_RUBY);
                            textArea.revalidate();
                            break;
                    }
                }
            });
            form.addLastField(comboLang,topPanel);
        }
        return topPanel;
    }

    public int getSelectedType() {
        String selected = (String) comboType.getSelectedItem();
        Set<Map.Entry<Integer, String>> kvp = Script.getTypesString().entrySet();
        for (Map.Entry<Integer,String> kv:kvp) {
            if(kv.getValue().equals(selected)){
                return kv.getKey();
            }
        }
        return Script.TYPE_ON_TEST_START;
    }

    public int getSelectedLang() {
        String selected = (String) comboLang.getSelectedItem();
        Set<Map.Entry<Integer, String>> kvp = Script.getTypesString().entrySet();
        for (Map.Entry<Integer,String> kv:kvp) {
            if(kv.getValue().equals(selected)){
                return kv.getKey();
            }
        }
        return Script.LANG_JS;
    }

    public RTextScrollPane getCenterCodeEditor() {
        if(centerCodeEditor == null){
            textArea = new RSyntaxTextArea(20, 60);
            textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
            textArea.setCodeFoldingEnabled(true);
            centerCodeEditor = new RTextScrollPane(textArea);
        }
        return centerCodeEditor;
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
//                    java.util.List<SequenceListModelObject> reqs = getSelectedSequences();
//                    String name = txtflowName.getText();
//                    String description = txtDescription.getText();
//                    String parameters = txtParam.getText();
//                    if(name.isEmpty() ){
//                        JOptionPane.showMessageDialog(DialogFlowNew.this,"Some required filed is not set.","Error",JOptionPane.ERROR_MESSAGE);
//                        return;
//                    }
//                    if(reqs.size()==0){
//                        JOptionPane.showMessageDialog(DialogFlowNew.this,"No sequence is added.","Error",JOptionPane.ERROR_MESSAGE);
//                        return;
//                    }
//                    if(Flow.isFlowNameUsed(name)){
//                        JOptionPane.showMessageDialog(DialogFlowNew.this,"Flow Name is Duplicated.","Error",JOptionPane.ERROR_MESSAGE);
//                        return;
//                    }
//                    if (!validateUpdate()) {
//                        JOptionPane.showMessageDialog(DialogFlowNew.this,"Invalid Data.","Error",JOptionPane.ERROR_MESSAGE);
//                        return;
//                    }
//                    updateSequenceDetail();
//                    try {
//                        java.util.List<Flow_Sequence> ts = new ArrayList<>();
//                        for (SequenceListModelObject req: reqs) {
//                            ts.add(req.getFlow_sequence());
//                        }
//                        Flow.insertFlow(new Flow(name, description ,parameters,ts));
//                        dissmiss();
//
//                    }catch (Exception x){
//                        BurpExtender.getInstance().getStdout().println("save Error "+x.getMessage() + "\n");
//                        x.printStackTrace(BurpExtender.getInstance().getStdout());
//                    }
                }
            });
            buttonsPanel.add(cancelBtn);
            buttonsPanel.add(addBtn);
        }
        return buttonsPanel;
    }
}
