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
    private JList instancesJlist;
    private JSplitPane splitPaneRequests;
    private DefaultListModel<RequestListModelObject> modelRequest;
    private JList requestJlist;
    private JTabbedPane tabs;
    private BurpExtender ext;
    private IMessageEditor requestViewer;
    private IMessageEditor responseViewer;
    private IMessageEditor requestTemplateViewer;
    private TableModelRequestIn modelRequestIn;
    private JTable jtableRequestIn;
    private TableModelResponseOut modelResponseOut;
    private JTable jtableResponseOut;
    private JTable jtableLocals;
    private TableModelResponseOut modelLocals;
    private TableModelResponseOut modelGlobals;
    private JTable jtableGlobals;
    private List<TestCaseInstance> testCaseInstances;
    private RequestListModelObject currentlyDisplayedRequest;
    private TestCaseInstance currentlyDisplayedInstance;
    private JButton actionBtn;
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
                    TestCaseInstance insta = new TestCaseInstance(testCase,maps, order);
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
            centerSplitPanel.setLeftComponent(new JScrollPane(getInstanceJListPanel()));
            centerSplitPanel.setRightComponent(getSplitPaneRequests());
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
                        actionBtn.setText("Cancel");
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
                // TODO clean old run results
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
                        for (RequestIn inPar : inPars) {
                            msg = DataUtils.applyParameter(msg,inPar,instance.getInitParamFor(inPar));
                        }
                        msg = DataUtils.changeHost(msg,url.getHost());
                        msg = DataUtils.changeReferer(msg,url.toString());
                        msg = DataUtils.changeUrlBase(msg,base1,base2);
                        msg = DataUtils.changeCookie(msg,cookie);
                        byte[] newRequest = DataUtils.buildRequest(msg);
                        IHttpService httpService = DataUtils.makeHttpService(url);
                        try {
                            Thread.sleep(Settings.DELAY);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        final IHttpRequestResponse response = ext.getCallbacks().makeHttpRequest(httpService,newRequest);
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
                    if(!instancesJlist.getSelectedValue().equals(obj.getTestInstance())) {
                        instancesJlist.setSelectedValue(obj.getTestInstance(), true);
                    }
                    modelRequest.addElement(obj);
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
                        updateRequest();
                    }
                }
            });
        }
        return instancesJlist;
    }

    private void updateRequest() {
        modelRequest.clear();
        for(RequestListModelObject obj: currentlyDisplayedInstance.getRequestModelItem()){
            modelRequest.addElement(obj);
        }
    }

    public JSplitPane getSplitPaneRequests() {
        if (splitPaneRequests == null) {
            splitPaneRequests = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

            splitPaneRequests.setDividerLocation(150);
            splitPaneRequests.setLeftComponent(new JScrollPane(getRequestJListPanel()));
            splitPaneRequests.setRightComponent(getTabs());
        }
        return splitPaneRequests;
    }

    public JList getRequestJListPanel() {
        if(requestJlist == null){
            modelRequest = new DefaultListModel<>();
            requestJlist = new JList(modelRequest);
            requestJlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            requestJlist.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if(e.getValueIsAdjusting()) {
                        updateRequestInfo();
                    }
                }
            });
        }
        return requestJlist;
    }

    private void updateRequestInfo() {
        currentlyDisplayedRequest = (RequestListModelObject) requestJlist.getSelectedValue();
        requestViewer.setMessage(currentlyDisplayedRequest.getRequest(), true);
        responseViewer.setMessage(currentlyDisplayedRequest.getResponse(), false);
        requestTemplateViewer.setMessage(currentlyDisplayedRequest.getTestRequest().getModifiedRequest(), true);
        // TODO update jTables
    }

    public JTabbedPane getTabs() {
        if(tabs == null){
            tabs = new JTabbedPane();
            updateTabs();
        }
        return tabs;
    }

    private void updateTabs() {
        tabs.removeAll();
        addTab_request();
        addTab_response();
        addTab_request_template();
        addTab_locals();
        addTab_globals();
        addTab_params_in();
        addTab_params_out();
    }

    public BurpExtender getExt() {
        if(ext ==null){
            ext= BurpExtender.getInstance();
        }
        return ext;
    }

    private void addTab_request() {
        IBurpExtenderCallbacks callbacks = getExt().getCallbacks();
        requestViewer = callbacks.createMessageEditor(new IMessageEditorController() {
            @Override
            public IHttpService getHttpService() {
                if(currentlyDisplayedRequest == null){
                    return null;
                }
                return currentlyDisplayedRequest.getHttpService();
            }

            @Override
            public byte[] getRequest() {
                if(currentlyDisplayedRequest == null){
                    return new byte[0];
                }
                return currentlyDisplayedRequest.getRequest();
            }

            @Override
            public byte[] getResponse() {
                if(currentlyDisplayedRequest == null){
                    return new byte[0];
                }
                return currentlyDisplayedRequest.getResponse();
            }
        }, false);
        tabs.addTab("Request", requestViewer.getComponent());
    }

    private void addTab_response() {
        IBurpExtenderCallbacks callbacks = getExt().getCallbacks();
        responseViewer = callbacks.createMessageEditor(new IMessageEditorController() {
            @Override
            public IHttpService getHttpService() {
                if(currentlyDisplayedRequest == null){
                    return null;
                }
                return currentlyDisplayedRequest.getHttpService();
            }

            @Override
            public byte[] getRequest() {
                if(currentlyDisplayedRequest == null){
                    return new byte[0];
                }
                return currentlyDisplayedRequest.getRequest();
            }

            @Override
            public byte[] getResponse() {
                if(currentlyDisplayedRequest == null){
                    return new byte[0];
                }
                return currentlyDisplayedRequest.getResponse();
            }
        }, true);
        tabs.addTab("Response", responseViewer.getComponent());
    }

    private void addTab_request_template() {
        IBurpExtenderCallbacks callbacks = getExt().getCallbacks();
        requestTemplateViewer = callbacks.createMessageEditor(new IMessageEditorController() {
            @Override
            public IHttpService getHttpService() {
                if(currentlyDisplayedRequest == null){
                    return null;
                }
                return null;
            }

            @Override
            public byte[] getRequest() {
                if(currentlyDisplayedRequest == null){
                    return new byte[0];
                }
                return currentlyDisplayedRequest.getTestRequest().getModifiedRequest();
            }

            @Override
            public byte[] getResponse() {
                if(currentlyDisplayedRequest == null){
                    return new byte[0];
                }
                return new byte[0];
            }
        }, false);
        tabs.addTab("Request Template", requestTemplateViewer.getComponent());
    }

    private void addTab_locals() {
        tabs.addTab("Locals", new JScrollPane(getJtableLocals()));
    }

    private void addTab_globals() {
        tabs.addTab("Globals", new JScrollPane(getJtableGlobals()));
    }

    private void addTab_params_in() {
        tabs.addTab("Params In", new JScrollPane(getJtableRequestIn()));
    }

    private void addTab_params_out() {
        tabs.addTab("Params Out", new JScrollPane(getJtableResponseOut()));
    }


    public JTable getJtableRequestIn() {
        if(jtableRequestIn == null){
            modelRequestIn = new TableModelRequestIn();
            jtableRequestIn = new JTable();
            jtableRequestIn.setModel(modelRequestIn);
        }
        return jtableRequestIn;
    }

    public JTable getJtableResponseOut() {
        if(jtableResponseOut == null){
            modelResponseOut = new TableModelResponseOut();
            jtableResponseOut = new JTable();
            jtableResponseOut.setModel(modelResponseOut);
        }
        return jtableResponseOut;
    }

    public JTable getJtableLocals() {
        if(jtableLocals== null){
            modelLocals = new TableModelResponseOut();
            jtableLocals = new JTable();
            jtableLocals.setModel(modelLocals);
        }
        return jtableLocals;
    }

    public JTable getJtableGlobals() {
        if(jtableGlobals== null){
            modelGlobals = new TableModelResponseOut();
            jtableGlobals = new JTable();
            jtableGlobals.setModel(modelGlobals);
        }
        return jtableGlobals;
    }
}
