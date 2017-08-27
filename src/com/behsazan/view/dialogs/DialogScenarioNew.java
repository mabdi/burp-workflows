package com.behsazan.view.dialogs;

import burp.BurpExtender;
import com.behsazan.model.DataUtils;
import com.behsazan.model.entity.Flow;
import com.behsazan.model.entity.Scenario;
import com.behsazan.model.settings.Settings;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractDialog;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 08/21/2017.
 */
public class DialogScenarioNew extends AbstractDialog {

    private JPanel formPanel;
    private JPanel buttonsPanel;
    private JComboBox<String> cmbFlow;
    private DefaultComboBoxModel<String> modelCombo;
    private JComboBox<String> cmbUrls;
    private DefaultComboBoxModel<String> modelParams;
    private JComboBox<String> cmbParams;
    private Map<String ,String[]> params;
    private JTextArea txtValues;
    private JTextField txtName;
    private JTextArea txtDescription;


    public DialogScenarioNew() {
        super();
        params = new HashMap<>();
    }

    @Override
    protected void initUI() {
        setSize(400, 300);
        setTitle("New Scenario");
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

            formUtility.addLabel("Name :", formPanel);
            txtName = new JTextField();
            formUtility.addLastField(txtName, formPanel);

            formUtility.addLabel("Description :", formPanel);
            txtDescription = new JTextArea(2,10);
            formUtility.addLastField(txtDescription, formPanel);

            formUtility.addLabel("Url :", formPanel);
            DefaultComboBoxModel<String> urls = new DefaultComboBoxModel<String>();
            for(String u: Settings.BASE_URLS){
                urls.addElement(u);
            }
            cmbUrls = new JComboBox<>(urls);
            formUtility.addLastField(cmbUrls, formPanel);

            formUtility.addLabel("Flow :", formPanel);
            modelCombo = new DefaultComboBoxModel<>();
            cmbFlow = new JComboBox<>(modelCombo);
            List<String> tests = Flow.getAllFlowName();
            for (String t: tests) {
                modelCombo.addElement(t);
            }
            formUtility.addLastField(cmbFlow, formPanel);

            formUtility.addLabel("Parameters: ",formPanel);
            modelParams = new DefaultComboBoxModel<>();
            cmbParams = new JComboBox<>(modelParams);
            cmbParams.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.DESELECTED) {
                        updateParams((String) e.getItem());
                    }
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        showParams((String) e.getItem());
                    }
                }
            });
            formUtility.addLastField(cmbParams,formPanel);

            formUtility.addLabel("Values: ",formPanel);
            txtValues = new JTextArea(5,10);
            formUtility.addLastField(txtValues,formPanel);

            formUtility.addLabel("",formPanel);
            JButton btnLoadFromFile = new JButton("Load from file");
            btnLoadFromFile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    final JFileChooser fc = new JFileChooser();
                    int returnVal = fc.showOpenDialog(DialogScenarioNew.this);
                    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fc.addChoosableFileFilter(new FileFilter() {
                        @Override
                        public boolean accept(File f) {
                            return f.getAbsolutePath().toLowerCase().endsWith(".txt");
                        }

                        @Override
                        public String getDescription() {
                            return "Text Files (*.txt)";
                        }
                    });
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        try {
                            List<String> lines = FileUtils.readLines(file, Charset.forName("UTF-8"));
                            for (String line :
                                    lines) {
                                appendLine(line);
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
            JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jp.add(btnLoadFromFile);
            formUtility.addLastField(jp,formPanel);
        }
        return formPanel;
    }


    private void appendLine(String line){
        txtValues.append(line);
        txtValues.append("\n");
    }

    private void updateParams(String name){
        String[] list = txtValues.getText().split("\n");
        params.put(name ,list);
    }

    private void showParams(String name){
        String[] list = params.get(name);
        txtValues.setText(StringUtils.join(list,'\n'));
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
                    String name = txtName.getText();
                    String description = txtDescription.getText();
                    String url = (String) cmbUrls.getSelectedItem();
                    String selectedFlow = (String) cmbFlow.getSelectedItem();
                    Flow flow = Flow.getByName(selectedFlow);
                    if(name.isEmpty() || url.isEmpty()){
                        JOptionPane.showMessageDialog(DialogScenarioNew.this,"Some required filed is not set.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if(DataUtils.isValidURL(url)){
                        JOptionPane.showMessageDialog(DialogScenarioNew.this,"Invalid URL.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        Scenario.insertScenario(new Scenario(-1, name,description, params, "", url, flow));
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