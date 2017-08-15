package com.behsazan.view.dialogs;

import burp.*;
import com.behsazan.model.adapters.TableModelRequestIn;
import com.behsazan.model.adapters.TableModelResponseOut;
import com.behsazan.model.entity.RequestIn;
import com.behsazan.model.entity.ResponseOut;
import com.behsazan.model.entity.TestCase_Request;
import com.behsazan.model.entity.TestCase_Sequence;
import com.behsazan.view.abstracts.AbstractDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by admin on 08/07/2017.
 */
public class DialogTestCaseRequests extends AbstractDialog implements IMessageEditorController {
    private TestCase_Sequence sequence;
    private TestCase_Sequence result;
    private JPanel buttonsPanel;
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

    public DialogTestCaseRequests(Component parent) {
        super(parent,false);
    }

    public TestCase_Sequence setData(TestCase_Sequence sequence){
        ArrayList<TestCase_Request> requests = new ArrayList<TestCase_Request>();
        for(TestCase_Request seq : sequence.getRequests()){
            requests.add(new TestCase_Request(seq.getId(),seq.getRequest(),seq.getInputParams(),seq.getOutputParams(),seq.getModifiedRequest()));
        }
        this.sequence = new TestCase_Sequence(sequence.getId(),sequence.getSequence(),sequence.getUrl(),
                sequence.getBase1(),sequence.getBase2(),sequence.getCookie(),requests);
        for (TestCase_Request obj :
                sequence.getRequests()) {
            modelRequests.addElement(obj);
        }
        setVisible(true);

        return result;
    }

    @Override
    protected void initUI() {
        setSize(1000, 700);
        setTitle("TestCase Sequence Requests");
        setLocationRelativeTo(getParentWindow());

        setLayout(new BorderLayout());
        add(getSplitPane(), BorderLayout.CENTER);
        add(getButtonsPanel(), BorderLayout.SOUTH);

    }

    public JPanel getButtonsPanel() {
        if(buttonsPanel==null){
            buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    result = null;dissmiss();
                }
            });
            JButton addBtn = new JButton("Ok");
            addBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    result = sequence;
                    dissmiss();

                }
            });
            buttonsPanel.add(cancelBtn);
            buttonsPanel.add(addBtn);
        }
        return buttonsPanel;
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
                    updateRequestDetail();
                    currentRequest = listRequests.getSelectedValue();
                    showRequestDetail();
                }
            });
        }
        return listRequests;
    }

    private void updateRequestDetail() {
        if(currentRequest==null) return;
        currentRequest.setModifiedRequest(requestViewer.getMessage());
    }

    private void showRequestDetail() {
        requestViewer.setMessage(currentRequest.getRequest().getRequest(), true);
        responseViewer.setMessage(currentRequest.getRequest().getResponse(),false);
        modelRequestIn.changeData(currentRequest.getInputParams());
        modelResponseOut.changeData(currentRequest.getOutputParams());
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

    public JTabbedPane getRequestTabs() {
        if(tabs == null){
            tabs = new JTabbedPane();
            IBurpExtenderCallbacks callbacks = BurpExtender.getInstance().getCallbacks();
            requestViewer = callbacks.createMessageEditor(this, true);
            responseViewer = callbacks.createMessageEditor(this, false);
            tabs.addTab("Request", requestViewer.getComponent());
            tabs.addTab("Response", responseViewer.getComponent());
        }
        return tabs;
    }

    @Override
    public IHttpService getHttpService() {
        return null;
    }

    @Override
    public byte[] getRequest() {
        return new byte[0];
    }

    @Override
    public byte[] getResponse() {
        return new byte[0];
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
                    DialogRequestInput dlg = new DialogRequestInput(DialogTestCaseRequests.this);
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
                    DialogRequestInput dlg = new DialogRequestInput(DialogTestCaseRequests.this);
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
                    DialogResponseOutput dlg = new DialogResponseOutput(DialogTestCaseRequests.this);
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
                    DialogResponseOutput dlg = new DialogResponseOutput(DialogTestCaseRequests.this);
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
}
