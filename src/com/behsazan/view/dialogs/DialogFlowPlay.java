package com.behsazan.view.dialogs;

import com.behsazan.controller.Controller;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.*;
import com.behsazan.view.abstracts.AbstractDialog;
import com.behsazan.view.panels.PanelPlayInstance;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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
    private JToggleButton actionBtn;
    private PanelPlayInstance mPlayInstancePanel;
    private boolean forceStop;

    public DialogFlowPlay(JPanel parent) {
        super(false);
        flow_runnings = new ArrayList<>();
    }

    public void setData(final int id){
        final DialogWaiting waitDialog = DialogWaiting.showWaitingDialog(this);
        final SwingWorker<Void,Flow_Running> worker = new SwingWorker<Void,Flow_Running>() {
            @Override
            protected Void doInBackground() throws Exception {
                DialogFlowPlay.this.flow = Flow.getById(id);
                flow_runnings = Controller.buildTestCaseInstances(DialogFlowPlay.this.flow, new Controller.BuildTestCaseInstancesListener() {
                    @Override
                    public void publishInstance(Flow_Running instance) {
                        publishInstance(instance);
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                worker.execute();
                waitDialog.setVisible(true);
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
        add(getCenterSplitPanel(), BorderLayout.CENTER);
        add(getButtonsPanel(), BorderLayout.SOUTH);
    }


    public JPanel getCenterSplitPanel() {
        if (centerPanel == null) {
            centerPanel = new JPanel(new BorderLayout());
            centerSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            centerSplitPanel.setDividerLocation(100);
            mPlayInstancePanel = new PanelPlayInstance();
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
            actionBtn = new JToggleButton("Run");
            actionBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(actionBtn.isSelected()){
                        forceStop = true;
                    }else {
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
                actionBtn.setSelected(false);
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
}
