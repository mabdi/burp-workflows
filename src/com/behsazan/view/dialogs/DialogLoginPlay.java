package com.behsazan.view.dialogs;

import burp.ICookie;
import com.behsazan.controller.Controller;
import com.behsazan.controller.Flow_Running;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.*;
import com.behsazan.model.settings.Settings;
import com.behsazan.view.abstracts.AbstractDialog;
import com.behsazan.view.panels.PanelFlowPlay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Created by admin on 08/02/2017.
 */
public class DialogLoginPlay extends AbstractDialog {

    private DialogWaiting pleaseWaitDialog;
    private JPanel buttonsPanel;
    private Flow_Running run;
    private JButton actionBtn;
    private PanelFlowPlay mPlayInstancePanel;
    private boolean forceStop;
    private Login login;
    private boolean is_running;

    public DialogLoginPlay() {
        super(false);
        is_running = false;
    }

    public void setData(final int id) {
        pleaseWaitDialog = DialogWaiting.showWaitingDialog(this);
        SwingWorker<Flow, Flow_Running> worker = new SwingWorker<Flow, Flow_Running>() {
            @Override
            protected Flow doInBackground() throws Exception {
                login = Login.getById(id);
                Map<String,String> map = new HashMap<>();
                map.put("username",login.getUsername());
                map.put("password",login.getPassword());
                run = new Flow_Running(login.getFlow(),login.getUrl(),map,1);
                return null;
            }

            @Override
            protected void done() {
                DialogWaiting.closeWaitingDialog(pleaseWaitDialog);
                mPlayInstancePanel.updateInstance(run);
            }
        };
        worker.execute();

        this.setVisible(true);

    }

    @Override
    protected void initUI() {
        setSize(800, 600);
        setTitle("Play Login");
        setLocationRelativeTo(getParentWindow());
        mPlayInstancePanel = new PanelFlowPlay();
        setLayout(new BorderLayout());
        add(mPlayInstancePanel, BorderLayout.CENTER);
        add(getButtonsPanel(), BorderLayout.SOUTH);
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
                runTestCase(run);
                return null;
            }

            private RequestListModelObject runTestCase(Flow_Running instance) {
                List<RequestListModelObject> requests = Controller.runTestCase(instance, new Controller.RunTestCaseListener() {
                    @Override
                    public boolean isRunFinished() {
                        return forceStop;
                    }

                    @Override
                    public void publishState(RequestListModelObject state) {
                        publish(state);
                    }
                });
                String lastCookie = "";
                for (RequestListModelObject req :
                        requests) {
                    for(ICookie cook: req.getRequestObject().getAnalysedResponse().getCookies()){
                        if(cook.getName().equals(Settings.getCookie())){
                            lastCookie = cook.getValue();
                        }
                    }
                }
                instance.updateGlobalVariable(login.getOutParam(), lastCookie);
                if (!Flow_Running.queryGlobalVariable(login.getOutParam()).isEmpty()) {
                    login.setLast_seen((int) new Date().getTime());
                    login.setSession(Flow_Running.queryGlobalVariable(login.getOutParam()));
                    Login.updateLogin(login);
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
