package com.behsazan.view.dialogs;

import burp.*;
import com.behsazan.model.DataUtils;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.adapters.TableModelRequestIn;
import com.behsazan.model.adapters.TableModelResponseOut;
import com.behsazan.model.entity.*;
import com.behsazan.model.settings.Settings;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractDialog;
import com.behsazan.view.panels.PanelPlayInstance;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by admin on 08/02/2017.
 */
public class DialogTestCasePlay extends AbstractDialog {

    private TestCase testCase;
    private JDialog pleaseWaitDialog;
    private JPanel centerPanel;
    private JSplitPane centerSplitPanel;
    private JPanel buttonsPanel;
    private DefaultListModel<TestCaseInstance> modelInstances;
    private TestCaseInstance currentlyDisplayedInstance;
    private JList instancesJlist;
    private List<TestCaseInstance> testCaseInstances;
    private JButton actionBtn;
    private PanelPlayInstance mPlayInstancePanel;
    private boolean testIsRunning;
    private boolean forceStop;

    public DialogTestCasePlay(JPanel parent) {
        super(parent,false);
        testCaseInstances = new ArrayList<>();
    }

    public void setData(final int id){
        showWaitingDialog();
        SwingWorker<TestCase,TestCaseInstance> worker = new SwingWorker<TestCase,TestCaseInstance>() {
            @Override
            protected TestCase doInBackground() throws Exception {
                TestCase tcase = TestCase.getById(id);
                List<Map<Integer,String>> pars = new ArrayList<>();
                pars.add(new HashMap<Integer,String>());
                for (TestCase_Sequence seq : tcase.getSeqs()) {
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
                }
                int order = 0;
                for(Map<Integer,String> maps: pars){
                    TestCaseInstance insta = new TestCaseInstance(tcase,maps, order);
                    testCaseInstances.add(insta);
                    publish(insta);
                }
                return tcase;
            }

            @Override
            protected void done() {
                try {
                    DialogTestCasePlay.this.testCase = get();
                    closeWaitingDialog();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void process(List<TestCaseInstance> chunks) {
                for (TestCaseInstance ins : chunks) {
                    modelInstances.addElement(ins);
                }
            }
        };
        worker.execute();

        this.setVisible(true);

    }

    private void closeWaitingDialog(){
        if(pleaseWaitDialog != null) {
            pleaseWaitDialog.dispose();
            pleaseWaitDialog = null;
        }
    }

    private void showWaitingDialog() {
        pleaseWaitDialog = new JDialog(this);
        JPanel panel = new JPanel();
        final JLabel dialogWaitlabel = new JLabel("Please wait...");
        panel.add(dialogWaitlabel );
        pleaseWaitDialog.add(panel);
        pleaseWaitDialog.setTitle("Please wait...");
        pleaseWaitDialog.setModalityType(ModalityType.APPLICATION_MODAL);
//        pleaseWaitDialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // TODO uncommect
        pleaseWaitDialog.pack();
        pleaseWaitDialog.setLocationRelativeTo(this);
    }

    @Override
    protected void initUI() {
        setSize(800,600);
        setTitle("Play TestCase");
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
            actionBtn = new JButton("Run");
            actionBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(testIsRunning){
                        forceStop = true;
                    }else {
                        forceStop = false;
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
        SwingWorker<Void,RequestListModelObject> worker = new SwingWorker<Void, RequestListModelObject>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (TestCaseInstance instance: testCaseInstances) {
                    if(forceStop){
                        break;
                    }
//                    instancesJlist.setSelectedValue(instance,true);
                    runTestCase(instance);
                }
                return null;
            }

            private void runTestCase(TestCaseInstance instance) {
                for(TestCase_Sequence seq : instance.getTestCase().getSeqs()){
                    if(forceStop){
                        break;
                    }
                    List<TestCase_Request> reqs = seq.getRequests();
                    String base1 = seq.getBase1();
                    String base2 = seq.getBase2();
                    String cookie = seq.getCookie();
                    URL url = seq.getUrl();
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
                        for (ResponseOut outPar : outPars) {
                            DataUtils.setOutParameters(DialogTestCasePlay.this,obj,outPar,instance);
                        }
                        instance.getRequestModelItem().add(obj);
                        publish(obj);
                    }
                }
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
                        currentlyDisplayedInstance = (TestCaseInstance) instancesJlist.getSelectedValue();
                        mPlayInstancePanel.updateInstance(currentlyDisplayedInstance);
                    }
                }
            });
        }
        return instancesJlist;
    }
}
