package com.behsazan.view.dialogs;

import burp.ICookie;
import com.behsazan.controller.Controller;
import com.behsazan.controller.Flow_Running;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.Flow;
import com.behsazan.model.entity.Login;
import com.behsazan.model.entity.Scenario;
import com.behsazan.model.settings.Settings;
import com.behsazan.view.abstracts.AbstractDialog;
import com.behsazan.view.panels.PanelFlowPlay;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 08/02/2017.
 */
public class DialogScenarioPlay extends AbstractDialog {

    private DialogWaiting pleaseWaitDialog;
    private JPanel buttonsPanel;
    private JButton actionBtn;
    private PanelFlowPlay mPlayInstancePanel;
    private boolean forceStop;
    private Scenario scenario;
    private boolean is_running;
    private List<Flow_Running> flow_runnings;
    private JPanel centerPanel;
    private JSplitPane centerSplitPanel;
    private DefaultListModel<Flow_Running> modelInstances;
    private JList instancesJlist;
    private Flow_Running currentlyDisplayedInstance;

    public DialogScenarioPlay() {
        super(false);
        is_running = false;
    }

    public void setData(final int id) {
        pleaseWaitDialog = DialogWaiting.showWaitingDialog(this);
        SwingWorker<Flow, Flow_Running> worker = new SwingWorker<Flow, Flow_Running>() {
            @Override
            protected Flow doInBackground() throws Exception {
                scenario = Scenario.getById(id);
                flow_runnings = Controller.buildTestCaseInstances(
                        scenario.getFlow(), scenario.getUrl(), scenario.getParams_map(), new Controller.BuildTestCaseInstancesListener() {
                    @Override
                    public void publishInstance(Flow_Running instance) {
                        publish(instance);
                    }
                });
                return null;
            }

            @Override
            protected void process(List<Flow_Running> chunks) {
                for (Flow_Running ins : chunks) {
                    modelInstances.addElement(ins);
                }
            }

            @Override
            protected void done() {
                DialogWaiting.closeWaitingDialog(pleaseWaitDialog);
            }
        };
        worker.execute();

        this.setVisible(true);

    }

    @Override
    protected void initUI() {
        setSize(800, 600);
        setTitle("Play Scenario");
        setLocationRelativeTo(getParentWindow());
        mPlayInstancePanel = new PanelFlowPlay();
        setLayout(new BorderLayout());
        add(getCenterSplitPanel(), BorderLayout.CENTER);
        add(getButtonsPanel(), BorderLayout.SOUTH);
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
            actionBtn = new JButton("Run");
            actionBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (is_running) {
                        forceStop = true;
                    } else {
                        is_running = true;
                        forceStop = false;
                        mPlayInstancePanel.modelRequestClear();
                        actionBtn.setText("Stop");
                        actionBtn.setSelected(true);
                        runTest();
                    }
                }
            });
            buttonsPanel.add(actionBtn);
            buttonsPanel.add(cancelBtn);
        }
        return buttonsPanel;
    }

    private void runTest() {
        SwingWorker<Void, RequestListModelObject> worker = new SwingWorker<Void, RequestListModelObject>() {
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
                    mPlayInstancePanel.addNewRequest(obj);
                }
            }

            @Override
            protected void done() {
                actionBtn.setText("Run");
                is_running = false;
            }
        };
        worker.execute();
    }
}
