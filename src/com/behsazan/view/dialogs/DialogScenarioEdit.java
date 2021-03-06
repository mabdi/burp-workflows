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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.behsazan.view.UIUtils.FormUtility;

/**
 * Created by admin on 08/21/2017.
 */
public class DialogScenarioEdit extends AbstractDialog {

    private JPanel formPanel;
    private JPanel buttonsPanel;
    private JComboBox<String> cmbFlow;
    private DefaultComboBoxModel<String> modelCombo;
    private Scenario scenario;
    private Map<String ,String[]> params;
    private JComboBox<String> cmbUrls;
    private DefaultListModel<String> modelParams;
    private JTextArea txtValues;
    private JTextField txtName;
    private JTextArea txtDescription;
    private JList<String> listParams;
    private List<Flow> mflows;

    public DialogScenarioEdit() {
        super(false);
    }

    public Map<String, String[]> getParams() {
        if(params==null)
            params = new HashMap<>();
        return params;
    }

    @Override
    protected void initUI() {
        setSize(800, 600);
        setTitle("Edit Scenario");
        setLocationRelativeTo(getParentWindow());
        installEscapeCloseOperation();
        setLayout(new BorderLayout());
        add(getFormPanel(), BorderLayout.CENTER);
        add(getButtonsPanel(), BorderLayout.SOUTH);
    }

    public void setData(int id){
        Scenario scenario = Scenario.getById(id);
        this.scenario = scenario;
        txtName.setText(scenario.getName());
        txtDescription.setText(scenario.getDescription());
        cmbUrls.setSelectedItem(scenario.getUrl());
        cmbFlow.setSelectedItem(scenario.getFlow().getName());
        params = scenario.getParams_map();
        for (String u : scenario.getFlow().getParametersExploded()) {
            modelParams.addElement(u);
        }
        if (modelParams.size() > 0) {
            listParams.setSelectedIndex(0);
        }
        setVisible(true);
    }

    public JPanel getFormPanel() {
        if(formPanel == null){
            formPanel = new JPanel();
            formPanel.setLayout(new GridBagLayout());
            FormUtility formUtility = new FormUtility(formPanel);

            formUtility.addLabel("Name :");
            txtName = new JTextField();
            txtName.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());
            formUtility.addLastField(txtName);

            formUtility.addLabel("Description :");
            txtDescription = new JTextArea(2,10);
            txtDescription.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());
            formUtility.addLastField(txtDescription);


            formUtility.addLabel("Url :");
            DefaultComboBoxModel<String> urls = new DefaultComboBoxModel<String>();
            for(String u: Settings.getBaseUrls()){
                urls.addElement(u);
            }
            cmbUrls = new JComboBox<>(urls);
            formUtility.addLastField(cmbUrls);

            formUtility.addLabel("Flow :");
            modelCombo = new DefaultComboBoxModel<>();
            cmbFlow = new JComboBox<>(modelCombo);
            mflows = Flow.getAllFlows();
            for (Flow t: mflows) {
                modelCombo.addElement(t.getName());
            }

            JButton btnShowParams = new JButton("Update Parameters");
            btnShowParams.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateParametersOnclick();
                }
            });
            JPanel jpr = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jpr.add(btnShowParams);
            formUtility.addMiddleField(cmbFlow);
            formUtility.addLastField(jpr);
            formUtility.addLabel("Parameters: ");

            modelParams = new DefaultListModel<>();
            listParams = new JList<>(modelParams);
            listParams.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listParams.addListSelectionListener(new ListSelectionListener() {
                int selectedIndex = -1;
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        if(selectedIndex>=0)
                            updateParams(modelParams.elementAt(selectedIndex));
                        selectedIndex = listParams.getSelectedIndex();
                        showParams(modelParams.elementAt(selectedIndex));
                    }
                }
            });
            listParams.setSelectedIndex(0);
//            formUtility.addMiddleField();

            txtValues = new JTextArea(5, 10);
            txtValues.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());

            JSplitPane splitp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitp.setLeftComponent(new JScrollPane(listParams));
            splitp.setRightComponent(new JScrollPane(txtValues));
            splitp.setDividerLocation(0.3);


            formUtility.addLastField( splitp );

            JButton btnLoadFromFile = new JButton("Load from file");
            btnLoadFromFile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    final JFileChooser fc = new JFileChooser();
                    int returnVal = fc.showOpenDialog(DialogScenarioEdit.this);
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
            JPanel jp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            jp.add(btnLoadFromFile);
            formUtility.addLastField(jp);

//            formUtility.addLabel("Parameters: ");
//            modelParams = new DefaultComboBoxModel<>();
//            cmbParams = new JComboBox<>(modelParams);
//            cmbParams.addItemListener(new ItemListener() {
//                @Override
//                public void itemStateChanged(ItemEvent e) {
//                    if (e.getStateChange() == ItemEvent.DESELECTED) {
//                        updateParams((String) e.getItem());
//                    }
//                    if (e.getStateChange() == ItemEvent.SELECTED) {
//                        showParams((String) e.getItem());
//                    }
//                }
//            });
//            formUtility.addLastField(cmbParams);
//
//            formUtility.addLabel("Values: ");
//            txtValues = new JTextArea(5,10);
//            txtValues.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());
//            formUtility.addLastField(txtValues);
//
//            formUtility.addLabel("");
//            JButton btnLoadFromFile = new JButton("Load from file");
//            btnLoadFromFile.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    final JFileChooser fc = new JFileChooser();
//                    int returnVal = fc.showOpenDialog(DialogScenarioEdit.this);
//                    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
//                    fc.addChoosableFileFilter(new FileFilter() {
//                        @Override
//                        public boolean accept(File f) {
//                            return f.getAbsolutePath().toLowerCase().endsWith(".txt");
//                        }
//
//                        @Override
//                        public String getDescription() {
//                            return "Text Files (*.txt)";
//                        }
//                    });
//                    if (returnVal == JFileChooser.APPROVE_OPTION) {
//                        File file = fc.getSelectedFile();
//                        try {
//                            List<String> lines = FileUtils.readLines(file, Charset.forName("UTF-8"));
//                            for (String line :
//                                    lines) {
//                                appendLine(line);
//                            }
//                        } catch (IOException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                }
//            });
//            JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
//            jp.add(btnLoadFromFile);
//            formUtility.addLastField(jp);
        }
        return formPanel;
    }

    private void updateParametersOnclick() {
        modelParams.removeAllElements();
        getParams().clear();
        Flow flow = mflows.get(cmbFlow.getSelectedIndex());
        for (String u : flow.getParametersExploded()) {
            modelParams.addElement(u);
        }
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
                    if(url.isEmpty()){
                        JOptionPane.showMessageDialog(DialogScenarioEdit.this,"Some required filed is not set.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!DataUtils.isValidURL(url)) {
                        JOptionPane.showMessageDialog(DialogScenarioEdit.this,"Invalid URL.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        scenario.setUrl(url);
                        scenario.setFlow(flow);
                        scenario.setParams_map(params);
                        scenario.setName(name);
                        scenario.setDescription(description);
                        Scenario.updateScenario(scenario);
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