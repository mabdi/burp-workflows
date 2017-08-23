package com.behsazan.view.panels;

import burp.BurpExtender;
import burp.IBurpExtenderCallbacks;
import burp.IMessageEditor;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.adapters.TableModelRequestIn;
import com.behsazan.model.adapters.TableModelResponseOut;
import com.behsazan.model.entity.TestCase;
import com.behsazan.model.entity.TestCaseInstance;
import com.behsazan.view.abstracts.AbstractPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;

/**
 * Created by admin on 08/21/2017.
 */
public class PanelPlayInstance extends AbstractPanel {

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
    private JTable jtableGlobals;

    private RequestListModelObject currentlyDisplayedRequest;
    private TestCaseInstance currentlyDisplayedInstance;

    @Override
    protected void initUI() {
        setLayout(new BorderLayout());
        add(getSplitPaneRequests(),BorderLayout.CENTER);
    }

    public void updateInstance(TestCaseInstance instance){
        currentlyDisplayedInstance = instance;
        updateRequest();
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
        if(currentlyDisplayedRequest==null)
            return;
        requestViewer.setMessage(currentlyDisplayedRequest.getRequest(), true);
        responseViewer.setMessage(currentlyDisplayedRequest.getResponse(), false);
        requestTemplateViewer.setMessage(currentlyDisplayedRequest.getTestRequest().getModifiedRequest(), true);
        jtableLocals.setModel(currentlyDisplayedInstance.localsToTableModel());
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
        requestViewer = callbacks.createMessageEditor(null, false);
        tabs.addTab("Request", requestViewer.getComponent());
    }

    private void addTab_response() {
        IBurpExtenderCallbacks callbacks = getExt().getCallbacks();
        responseViewer = callbacks.createMessageEditor(null, false);
        tabs.addTab("Response", responseViewer.getComponent());
    }

    private void addTab_request_template() {
        IBurpExtenderCallbacks callbacks = getExt().getCallbacks();
        requestTemplateViewer = callbacks.createMessageEditor(null, false);
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
            jtableLocals = new JTable();
        }
        return jtableLocals;
    }

    public JTable getJtableGlobals() {
        if(jtableGlobals== null){
            jtableGlobals = new JTable();
            jtableGlobals.setModel(TestCaseInstance.globalsToTableModel());
        }
        return jtableGlobals;
    }

    private void updateRequest() {
        modelRequest.clear();
        for(RequestListModelObject obj: currentlyDisplayedInstance.getRequestModelItem()){
            modelRequest.addElement(obj);
        }
        jtableLocals.setModel(currentlyDisplayedInstance.localsToTableModel());
    }

    public void modelRequestClear() {
        modelRequest.clear();
    }

    public void addNewRequest(RequestListModelObject obj) {

        modelRequest.addElement(obj);
    }
}
