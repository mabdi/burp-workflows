package com.behsazan.view.panels;

import burp.BurpExtender;
import burp.IBurpExtenderCallbacks;
import burp.IMessageEditor;
import com.behsazan.model.adapters.TableModelRequestIn;
import com.behsazan.model.adapters.TableModelResponseOut;
import com.behsazan.model.entity.RequestIn;
import com.behsazan.model.entity.ResponseOut;
import com.behsazan.model.entity.TestCase_Request;
import com.behsazan.model.entity.TestCase_Sequence;
import com.behsazan.view.abstracts.AbstractPanel;
import com.behsazan.view.dialogs.DialogRequestInput;
import com.behsazan.view.dialogs.DialogResponseOutput;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by admin on 08/21/2017.
 */
public class PanelTestCaseRequests extends AbstractPanel {
    private JSplitPane splitPane;
    private JList<TestCase_Request> listRequests;
    private JSplitPane verticalSplitPane;
    private DefaultListModel<TestCase_Request> modelRequests;
    private JTabbedPane tabs;
    private IMessageEditor requestViewer;
    private IMessageEditor responseViewer;
    private JTabbedPane parametersTabs;
    private JPanel requestInputPanel;
    private JPanel responseOutputPanel;
    private JTable jtableResponseOut;
    private JTable jtableRequestIn;
    private TableModelRequestIn modelRequestIn;
    private TableModelResponseOut modelResponseOut;
    private TestCase_Request currentRequest;

    @Override
    protected void initUI() {
        setLayout(new BorderLayout());
        add(getSplitPane(), BorderLayout.CENTER);
    }

    public JSplitPane getSplitPane() {
        if (splitPane == null) {
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setLeftComponent(new JScrollPane(getRequestList()) );
            splitPane.setRightComponent(getVerticalSplitPane());
            splitPane.setDividerLocation(0.3);
        }
        return splitPane;
    }

    public JList getRequestList() {
        if(listRequests == null){
            modelRequests = new DefaultListModel<>();
            listRequests = new JList<>(modelRequests);
            listRequests.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listRequests.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if(e.getValueIsAdjusting()) {
                        updateRequestDetail();
                        currentRequest = listRequests.getSelectedValue();
                        showRequestDetail();
                    }
                }
            });
        }
        return listRequests;
    }


    public JSplitPane getVerticalSplitPane() {
        if (verticalSplitPane == null) {
            verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            verticalSplitPane.setTopComponent(getRequestTabs());
            verticalSplitPane.setBottomComponent(getParametersTabs());
            verticalSplitPane.setResizeWeight(0.5);
        }
        return verticalSplitPane;
    }


    public void updateRequestDetail() {
        if(currentRequest==null) return;
        currentRequest.setModifiedRequest(requestViewer.getMessage());
    }

    private void showRequestDetail() {
        if(currentRequest.getModifiedRequest()==null){
            currentRequest.setModifiedRequest(currentRequest.getRequest().getRequest());
        }
        requestViewer.setMessage(currentRequest.getModifiedRequest(), true);
        responseViewer.setMessage(currentRequest.getRequest().getResponse(),false);
        modelRequestIn.changeData(currentRequest.getInputParams());
        modelResponseOut.changeData(currentRequest.getOutputParams());
    }

    public JTabbedPane getRequestTabs() {
        if(tabs == null){
            tabs = new JTabbedPane();
            IBurpExtenderCallbacks callbacks = BurpExtender.getInstance().getCallbacks();
            requestViewer = callbacks.createMessageEditor(null, true);
            responseViewer = callbacks.createMessageEditor(null, false);
            tabs.addTab("Request", requestViewer.getComponent());
            tabs.addTab("Response", responseViewer.getComponent());
        }
        return tabs;
    }

    public JTabbedPane getParametersTabs() {
        if (parametersTabs == null) {
            parametersTabs = new JTabbedPane();

            parametersTabs.addTab("Request Input", getRequestInputPanel());
            parametersTabs.addTab("Response Output", getResponseOutputPanel());
        }
        return parametersTabs;
    }

    public JPanel getRequestInputPanel() {
        if (requestInputPanel == null) {
            requestInputPanel = new JPanel(new BorderLayout());
            JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton addSequence = new JButton("+");
            addSequence.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(listRequests.getSelectedIndex()<0){
                        return;
                    }
                    DialogRequestInput dlg = new DialogRequestInput(PanelTestCaseRequests.this);
                    RequestIn requestIn = dlg.getData(listRequests.getSelectedValue());
                    if(requestIn!=null) {
                        listRequests.getSelectedValue().addInputParam(requestIn);
                        modelRequestIn.fireTableDataChanged();
                    }
                }
            });
            JButton edit = new JButton("?");
            edit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(getJtableRequestIn().getSelectedRow()<0){
                        return;
                    }
                    DialogRequestInput dlg = new DialogRequestInput(PanelTestCaseRequests.this);
                    dlg.getEditData(modelRequestIn.getItem(getJtableRequestIn().getSelectedRow()));
                }
            });
            JButton removeSequence = new JButton("-");
            removeSequence.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(getJtableRequestIn().getSelectedRow()<0){
                        return;
                    }
                    listRequests.getSelectedValue().getInputParams().remove(getJtableRequestIn().getSelectedRow());
                    modelRequestIn.fireTableDataChanged();
                }
            });
            btns.add(addSequence);
            btns.add(edit);
            btns.add(removeSequence);
            requestInputPanel.add(btns, BorderLayout.NORTH);
            requestInputPanel.add(new JScrollPane(getJtableRequestIn()), BorderLayout.CENTER);
        }
        return requestInputPanel;
    }

    public JPanel getResponseOutputPanel() {
        if (responseOutputPanel == null) {
            responseOutputPanel = new JPanel(new BorderLayout());
            JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton addSequence = new JButton("+");
            addSequence.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(listRequests.getSelectedIndex()<0){
                        return;
                    }
                    DialogResponseOutput dlg = new DialogResponseOutput(PanelTestCaseRequests.this);
                    ResponseOut responseOut = dlg.getData(listRequests.getSelectedValue());
                    if(responseOut != null){
                        listRequests.getSelectedValue().addOutputParam(responseOut);
                        modelResponseOut.fireTableDataChanged();
                    }
                }
            });
            JButton edit = new JButton("?");
            edit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(getJtableResponseOut().getSelectedRow()<0){
                        return;
                    }
                    DialogResponseOutput dlg = new DialogResponseOutput(PanelTestCaseRequests.this);
                    dlg.getEditData(modelResponseOut.getItem(getJtableResponseOut().getSelectedRow()));
                }
            });
            JButton removeSequence = new JButton("-");
            removeSequence.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(getJtableResponseOut().getSelectedRow()<0){
                        return;
                    }
                    listRequests.getSelectedValue().getOutputParams().remove(getJtableResponseOut().getSelectedRow());
                    modelResponseOut.fireTableDataChanged();
                }
            });
            btns.add(addSequence);
            btns.add(edit);
            btns.add(removeSequence);
            responseOutputPanel.add(btns, BorderLayout.NORTH);
            responseOutputPanel.add(new JScrollPane(getJtableResponseOut()), BorderLayout.CENTER);
        }
        return responseOutputPanel;
    }

    public JTable getJtableResponseOut() {
        if(jtableResponseOut == null){
            modelResponseOut = new TableModelResponseOut();
            jtableResponseOut = new JTable();
            jtableResponseOut.setModel(modelResponseOut);
        }
        return jtableResponseOut;
    }

    public JTable getJtableRequestIn() {
        if(jtableRequestIn == null){
            modelRequestIn = new TableModelRequestIn();
            jtableRequestIn = new JTable();
            jtableRequestIn.setModel(modelRequestIn);
        }
        return jtableRequestIn;
    }

    public void setData(TestCase_Sequence sequence) {
        for (TestCase_Request obj : sequence.getRequests()) {
            modelRequests.addElement(obj);
        }
    }
}
