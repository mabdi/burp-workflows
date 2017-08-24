package com.behsazan.view.dialogs;

import burp.BurpExtender;
import burp.ICookie;
import burp.IHttpRequestResponse;
import burp.IHttpService;
import com.behsazan.controller.Controller;
import com.behsazan.model.DataUtils;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.*;
import com.behsazan.model.settings.Settings;
import com.behsazan.view.abstracts.AbstractDialog;
import com.behsazan.view.panels.PanelPlayInstance;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Created by admin on 08/02/2017.
 */
public class DialogLoginPlay extends AbstractDialog {

    private DialogWaiting pleaseWaitDialog;
    private JPanel buttonsPanel;
    private TestCaseInstance currentlyDisplayedInstance;
    private JToggleButton actionBtn;
    private PanelPlayInstance mPlayInstancePanel;
    private boolean forceStop;
    private Login login;

    public DialogLoginPlay(JPanel parent) {
        super(parent, false);
    }

    public void setData(final int id) {
        pleaseWaitDialog = DialogWaiting.showWaitingDialog(this);
        SwingWorker<TestCase, TestCaseInstance> worker = new SwingWorker<TestCase, TestCaseInstance>() {
            @Override
            protected TestCase doInBackground() throws Exception {
                login = Login.getById(id);
                List<TestCaseInstance> instances = Controller.buildTestCaseInstances(login.getTestCase(), null);
                currentlyDisplayedInstance = instances.get(0);
                return null;
            }

            @Override
            protected void done() {
                DialogWaiting.closeWaitingDialog(pleaseWaitDialog);
                mPlayInstancePanel.updateInstance(currentlyDisplayedInstance);
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
        mPlayInstancePanel = new PanelPlayInstance();
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
            actionBtn = new JToggleButton("Run");
            actionBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (actionBtn.isSelected()) {
                        forceStop = true;
                    } else {
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
                runTestCase(currentlyDisplayedInstance);
                return null;
            }

            private RequestListModelObject runTestCase(TestCaseInstance instance) {
                TestCase_Sequence seq = instance.getTestCase().getSeqs().get(0);
                instance.updateLocalVariable("@@username@@", login.getUsername());
                instance.updateLocalVariable("@@password@@", login.getPassword());
                List<RequestListModelObject> requests = Controller.runTestCase(DialogLoginPlay.this, instance, new Controller.RunTestCaseListener() {
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
                        if(cook.getName().equals(Settings.SESSION_COOKIENAME)){
                            lastCookie = cook.getValue();
                        }
                    }
                }
                instance.updateGlobalVariable(login.getOutParam(), lastCookie);
                if (!TestCaseInstance.queryGlobalVariable(login.getOutParam()).isEmpty()) {
                    login.setLast_seen((int) new Date().getTime());
                    login.setSession(TestCaseInstance.queryGlobalVariable(login.getOutParam()));
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
            }
        };
        worker.execute();
    }
}
