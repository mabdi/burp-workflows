package com.behsazan.view.dialogs;

import burp.BurpExtender;
import burp.ICookie;
import burp.IHttpRequestResponse;
import burp.IHttpService;
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
    private JButton actionBtn;
    private PanelPlayInstance mPlayInstancePanel;
    private boolean testIsRunning;
    private boolean forceStop;
    private Login login;

    public DialogLoginPlay(JPanel parent) {
        super(parent,false);
    }

    public void setData(final int id){
        pleaseWaitDialog = DialogWaiting.showWaitingDialog(this);
        SwingWorker<TestCase,TestCaseInstance> worker = new SwingWorker<TestCase,TestCaseInstance>() {
            @Override
            protected TestCase doInBackground() throws Exception {
                login = Login.getById(id);
                TestCase tcase = login.getTestCase();
                List<Map<Integer,String>> pars = new ArrayList<>();
                pars.add(new HashMap<Integer,String>());
                TestCase_Sequence seq = tcase.getSeqs().get(0);
                for (TestCase_Request req : seq.getRequests()) {
                    for (RequestIn inp : req.getInputParams()) {
                        List<Map<Integer,String>> newPars = new ArrayList<>();
                        for(int i=0;i<inp.getTxtValueLines().length;i++){
                            if(i==0){
                                for (Map<Integer,String> maps: pars) {
                                    maps.put(inp.getId(),inp.getTxtValueLines()[0]);
                                }
                            }else{
                                for (Map<Integer,String> maps: pars) {
                                    HashMap<Integer, String> hashMap = new HashMap<>(maps);
                                    hashMap.put(inp.getId(),inp.getTxtValueLines()[i]);
                                    newPars.add(hashMap);
                                }
                            }
                        }
                        for(Map<Integer,String> maps: newPars){
                            pars.add(maps);
                        }
                    }
                }
                int order = 0;
                for(Map<Integer,String> maps: pars){
                    TestCaseInstance insta = new TestCaseInstance(tcase,maps, order);
                    publish(insta);
                }
                return tcase;
            }

            @Override
            protected void done() {
                DialogWaiting.closeWaitingDialog(pleaseWaitDialog);
            }

            @Override
            protected void process(List<TestCaseInstance> chunks) {
                currentlyDisplayedInstance = chunks.get(0);
                mPlayInstancePanel.updateInstance(currentlyDisplayedInstance);
            }
        };
        worker.execute();

        this.setVisible(true);

    }

    @Override
    protected void initUI() {
        setSize(800,600);
        setTitle("Play Login");
        setLocationRelativeTo(getParentWindow());
        mPlayInstancePanel = new PanelPlayInstance();
        setLayout(new BorderLayout());
        add(mPlayInstancePanel, BorderLayout.CENTER);
        add(getButtonsPanel(), BorderLayout.SOUTH);
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
            actionBtn = new JButton("Run");
            actionBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(testIsRunning){
                        forceStop = true;
                        testIsRunning = false;
                    }else {
                        forceStop = false;
                        testIsRunning = true;
                        mPlayInstancePanel.modelRequestClear();
                        actionBtn.setText("Stop");
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
        SwingWorker<Void ,RequestListModelObject> worker = new SwingWorker<Void , RequestListModelObject>() {
            @Override
            protected Void doInBackground() throws Exception {
                runTestCase(currentlyDisplayedInstance);
                return null;
            }

            private RequestListModelObject runTestCase(TestCaseInstance instance) {
                TestCase_Sequence seq = instance.getTestCase().getSeqs().get(0);
                currentlyDisplayedInstance.updateLocalVariable("@@username@@",login.getUsername());
                currentlyDisplayedInstance.updateLocalVariable("@@password@@",login.getPassword());

                List<TestCase_Request> reqs = seq.getRequests();
                String base1 = seq.getBase1();
                String base2 = login.getBase();
                String cookie = "";
                URL url = null;
                try {
                    url = new URL(login.getUrl());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                String lastCookie = null;
                for (TestCase_Request req: reqs) {
                    if(forceStop){
                        break;
                    }
                    List<RequestIn> inPars = req.getInputParams();
                    List<ResponseOut> outPars = req.getOutputParams();
                    byte[] modReq = req.getModifiedRequest();
                    String[] msg = DataUtils.ExplodeRequest(modReq);
                    msg = DataUtils.changeHost(msg,url.toString());
                    msg = DataUtils.changeReferer(msg,url.toString());
                    msg = DataUtils.changeUrlBase(msg,base1,base2);
                    if(!cookie.isEmpty()) {
                        msg = DataUtils.changeCookie(msg, cookie);
                    }
                    for (RequestIn inPar : inPars) {
                        msg = DataUtils.applyParameter(msg,inPar,instance.getInitParamFor(inPar));
                    }
                    byte[] newRequest = DataUtils.buildRequest(msg);
                    IHttpService httpService = DataUtils.makeHttpService(url);
                    try {
                        Thread.sleep(Settings.DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final IHttpRequestResponse response = BurpExtender.getInstance().getCallbacks().makeHttpRequest(httpService,newRequest);
                    RequestListModelObject obj = new RequestListModelObject(response);
                    obj.setTestInstance(instance);
                    obj.setTestRequest(req);
                    for (ICookie coo : obj.getRequestObject().getAnalysedResponse().getCookies()) {
                        if(coo.getName().equals("JSESSIONID")){
                            lastCookie = coo.getValue();
                        }
                    }
                    for (ResponseOut outPar : outPars) {
                        DataUtils.setOutParameters(DialogLoginPlay.this,obj,outPar,instance);
                    }
                    instance.getRequestModelItem().add(obj);
                    publish(obj);
                }
                instance.updateGlobalVariable(login.getOutParam(),lastCookie);
                if(!TestCaseInstance.queryGlobalVariable(login.getOutParam()).isEmpty()) {
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
