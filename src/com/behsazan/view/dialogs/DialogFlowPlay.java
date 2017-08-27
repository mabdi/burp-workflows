package com.behsazan.view.dialogs;

import com.behsazan.controller.Controller;
import com.behsazan.controller.Flow_Running;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.*;
import com.behsazan.model.settings.Settings;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractDialog;
import com.behsazan.view.panels.PanelFlowPlay;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 08/02/2017.
 */
public class DialogFlowPlay extends AbstractDialog {

    private Flow flow;
    private JPanel centerPanel;
    private JSplitPane centerSplitPanel;
    private JPanel buttonsPanel;
    private DefaultListModel<Flow_Running> modelInstances;
    private Flow_Running currentlyDisplayedInstance;
    private JList instancesJlist;
    private List<Flow_Running> flow_runnings;
    private JButton actionBtn;
    private PanelFlowPlay mPlayInstancePanel;
    private boolean forceStop;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel paramPanel;
    private JComboBox<String> cmbUrls;
    private JComboBox<String> cmbParams;
    private JTextArea txtValues;
    private Map<String ,String[]> params;
    private boolean testIsRunning;
    private DefaultComboBoxModel<String> modelParams;

    public DialogFlowPlay() {
        super(false);
        flow_runnings = new ArrayList<>();
        params = new HashMap<>();
        testIsRunning = false;
    }

    public void setData(final int id){
        final DialogWaiting waitDialog = DialogWaiting.showWaitingDialog(this);
        final SwingWorker<Void,Flow_Running> worker = new SwingWorker<Void,Flow_Running>() {
            @Override
            protected Void doInBackground() throws Exception {
                DialogFlowPlay.this.flow = Flow.getById(id);
                return null;
            }

            @Override
            protected void done() {
                DialogWaiting.closeWaitingDialog(waitDialog);
            }
        };
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                worker.execute();
                waitDialog.setVisible(true);
                for(String u: flow.getParametersExploded()){
                    modelParams.addElement(u);
                }
            }
        });
        this.setVisible(true);

    }

    @Override
    protected void initUI() {
        setSize(800,600);
        setTitle("Play Flow");
        setLocationRelativeTo(getParentWindow());

        setLayout(new BorderLayout());
        add(getCardPanel(), BorderLayout.CENTER);
        add(getButtonsPanel(), BorderLayout.SOUTH);
    }

    private JPanel getCardPanel() {
        if (cardPanel == null) {
            cardPanel = new JPanel();
            cardLayout = new CardLayout();
            cardPanel.setLayout(cardLayout);
            cardPanel.add(getParamPanel());
            cardPanel.add(getCenterSplitPanel());
            cardLayout.first(cardPanel);

        }
        return cardPanel;
    }

    public JPanel getCenterSplitPanel() {
        if (centerPanel == null) {
            centerPanel = new JPanel(new BorderLayout());
            centerSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            centerSplitPanel.setDividerLocation(100);
            mPlayInstancePanel = new PanelFlowPlay();
            centerSplitPanel.setLeftComponent(new JScrollPane(getInstanceJListPanel()));
            centerSplitPanel.setRightComponent(mPlayInstancePanel);
            centerPanel.add(centerSplitPanel, BorderLayout.CENTER);
        }
        return centerPanel;
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
            actionBtn = new JButton("Next");
            actionBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(actionBtn.getText().equalsIgnoreCase("next")){
                        updateParams((String) cmbParams.getSelectedItem());
                        cardLayout.next(cardPanel);
                        actionBtn.setText("Run");
                        calcInstances();
                        return;
                    }
                    if(actionBtn.getText().equalsIgnoreCase("run") ||
                            actionBtn.getText().equalsIgnoreCase("stop")){
                        if(testIsRunning){
                            forceStop = true;
                        }else {
                            testIsRunning = true;
                            forceStop = false;
                            mPlayInstancePanel.modelRequestClear();
                            actionBtn.setText("Stop");
                            actionBtn.setSelected(true);
                            runTest();
                        }
                    }
                }
            });

            buttonsPanel.add(actionBtn);
            buttonsPanel.add(cancelBtn);
        }
        return buttonsPanel;
    }

    private void runTest() {
        SwingWorker<Void,RequestListModelObject> worker = new SwingWorker<Void, RequestListModelObject>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (Flow_Running instance: flow_runnings) {
                    if(forceStop){
                        break;
                    }
                    Controller.runTestCase(instance, new Controller.RunTestCaseListener() {
                        @Override
                        public boolean isRunFinished() {
                            return forceStop;
                        }

                        @Override
                        public void publishState(RequestListModelObject state) {
                            publish(state);
                        }
                    });
                }
                return null;
            }


            @Override
            protected void process(List<RequestListModelObject> chunks) {
                for (RequestListModelObject obj : chunks) {
                    if(instancesJlist.getSelectedValue() == null || !instancesJlist.getSelectedValue().equals(obj.getTestInstance())) {
                        instancesJlist.setSelectedValue(obj.getTestInstance(), true);
                    }
                    mPlayInstancePanel.addNewRequest(obj);
//                    requestJlist.setSelectedValue(obj,true);
                }
            }

            @Override
            protected void done() {
                actionBtn.setText("Run");
                testIsRunning = false;
            }
        };
        worker.execute();

    }

    public JList getInstanceJListPanel() {
        if(instancesJlist == null){
            modelInstances = new DefaultListModel<>();
            instancesJlist = new JList(modelInstances);
            instancesJlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            instancesJlist.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if(e.getValueIsAdjusting()) {
                        currentlyDisplayedInstance = (Flow_Running) instancesJlist.getSelectedValue();
                        mPlayInstancePanel.updateInstance(currentlyDisplayedInstance);
                    }
                }
            });
        }
        return instancesJlist;
    }

    public JPanel getParamPanel() {
        if(paramPanel == null){
            paramPanel = new JPanel(new GridBagLayout());
            UIUtils.FormUtility formUtility = new UIUtils.FormUtility();

            formUtility.addLabel("Base URL: ",paramPanel);
            DefaultComboBoxModel<String> urls = new DefaultComboBoxModel<String>();
            for(String u: Settings.BASE_URLS){
                urls.addElement(u);
            }
            cmbUrls = new JComboBox<>(urls);
            formUtility.addLastField(cmbUrls,paramPanel);

            formUtility.addLabel("Parameters: ",paramPanel);
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
            formUtility.addLastField(cmbParams,paramPanel);

            formUtility.addLabel("Values: ",paramPanel);
            txtValues = new JTextArea(5,10);
            formUtility.addLastField(txtValues,paramPanel);

            formUtility.addLabel("",paramPanel);
            JButton btnLoadFromFile = new JButton("Load from file");
            btnLoadFromFile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    final JFileChooser fc = new JFileChooser();
                    int returnVal = fc.showOpenDialog(DialogFlowPlay.this);
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
            formUtility.addLastField(jp,paramPanel);
        }
        return paramPanel;
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

    private void calcInstances(){
        final DialogWaiting waitDialog = DialogWaiting.showWaitingDialog(this);
        final SwingWorker<Void,Flow_Running> worker = new SwingWorker<Void,Flow_Running>() {
            @Override
            protected Void doInBackground() throws Exception {
                String baseUrl = (String) cmbUrls.getSelectedItem();
                flow_runnings = Controller.buildTestCaseInstances(DialogFlowPlay.this.flow,baseUrl,params, new Controller.BuildTestCaseInstancesListener() {
                    @Override
                    public void publishInstance(Flow_Running instance) {
                        publish(instance);
                    }
                });
                return null;
            }

            @Override
            protected void done() {
                DialogWaiting.closeWaitingDialog(waitDialog);
            }

            @Override
            protected void process(List<Flow_Running> chunks) {
                for (Flow_Running ins : chunks) {
                    modelInstances.addElement(ins);
                }
            }
        };
        worker.execute();
        waitDialog.setVisible(true);
    }
}
