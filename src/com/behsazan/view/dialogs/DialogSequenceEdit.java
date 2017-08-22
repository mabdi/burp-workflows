package com.behsazan.view.dialogs;

import burp.*;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.Request;
import com.behsazan.model.entity.Sequence;
import com.behsazan.model.sqlite.SqliteHelper;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

/**
 * Created by admin on 07/29/2017.
 */
public class DialogSequenceEdit extends AbstractDialog implements IMessageEditorController {


    private Sequence sequence;
    private IBurpExtenderCallbacks callbacks;
    private JTextField sqName;
    private IMessageEditor requestViewer;
    private IMessageEditor responseViewer;
    private DefaultListModel<RequestListModelObject> modelAllRequests;
    private JList listAllRequests;
    private RequestListModelObject currentlyDisplayedItem;

    public DialogSequenceEdit(JPanel parent,int id) {
        super(parent,false);
        setSequence(id);
        setVisible(true);
    }

    private void setSequence(int id) {
        this.sequence = Sequence.getById(id);
        setData();
    }

    private void setData() {
        this.sqName.setText(sequence.getName());
        this.sqName.repaint();
        List<Request> requests = sequence.getRequest();
        for (Request rq: requests) {
            modelAllRequests.addElement(new RequestListModelObject(rq));
        }
        this.listAllRequests.repaint();
    }

    @Override
    protected void initUI() {
        setSize(800,600);
        setTitle("View/Edit Sequence");
        setLocationRelativeTo(getParentWindow());

        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Sequence Name: "));
        sqName = new JTextField("",40);
        topPanel.add(sqName);
        JButton updateName = new JButton("Update Name");
        updateName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = sqName.getText();
                if(name.isEmpty()){
                    JOptionPane.showMessageDialog(DialogSequenceEdit.this,"Sequence name is not set.","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(Sequence.isSequenceNameUsed(name)){
                    JOptionPane.showMessageDialog(DialogSequenceEdit.this,"Sequence Name is Duplicated.","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    Sequence.updateSequenceName(sequence.getId(),name);
                    dissmiss();
                } catch (SQLException e1) {
                    UIUtils.showGenerealError(DialogSequenceEdit.this);
                }
            }
        });
        topPanel.add(updateName);
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dissmiss();
            }
        });
        JButton updateRequest = new JButton("Update Request");
        updateRequest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!requestViewer.isMessageModified()){
                    int opt = JOptionPane.showConfirmDialog(DialogSequenceEdit.this,"Message is not modified. Do you want to force update?","Not Modifiend",JOptionPane.YES_NO_OPTION);
                    if(opt==JOptionPane.NO_OPTION){
                        return;
                    }
                }
                if(requestViewer.getMessage().length ==0){
                    JOptionPane.showMessageDialog(DialogSequenceEdit.this,"Messeage is Empty.","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                currentlyDisplayedItem.getRequestObject().setRequest(requestViewer.getMessage());
                try {
                    Request.updateRequestRequest(currentlyDisplayedItem.getRequestObject().getId(),currentlyDisplayedItem.getRequestObject().getRequest());
                    JOptionPane.showMessageDialog(DialogSequenceEdit.this,"Request Updated.","Done",JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException e1) {
                    UIUtils.showGenerealError(DialogSequenceEdit.this);
                }


            }
        });
        JButton updateResponse = new JButton("Update Response");
        updateResponse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!responseViewer.isMessageModified()){
                    int opt = JOptionPane.showConfirmDialog(DialogSequenceEdit.this,"Message is not modified. Do you want to force update?","Not Modifiend",JOptionPane.YES_NO_OPTION);
                    if(opt==JOptionPane.NO_OPTION){
                        return;
                    }
                }
                if(responseViewer.getMessage().length ==0){
                    JOptionPane.showMessageDialog(DialogSequenceEdit.this,"Messeage is Empty.","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                currentlyDisplayedItem.getRequestObject().setResponse(responseViewer.getMessage());
                try {
                    Request.updateRequestResponse(currentlyDisplayedItem.getRequestObject().getId(),currentlyDisplayedItem.getRequestObject().getResponse());
                    JOptionPane.showMessageDialog(DialogSequenceEdit.this,"Response Updated.","Done",JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException e1) {
                    UIUtils.showGenerealError(DialogSequenceEdit.this);
                }

            }
        });
        closePanel.add(updateRequest);
        closePanel.add(updateResponse);
        closePanel.add(closeButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(0.5);

        modelAllRequests = new DefaultListModel<>();
        listAllRequests = new JList(modelAllRequests);
        listAllRequests.setVisibleRowCount(10);
        listAllRequests.setFixedCellHeight(20);
        listAllRequests.setFixedCellWidth(140);
        listAllRequests.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listAllRequests.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(e.getValueIsAdjusting()) {
                    currentlyDisplayedItem = (RequestListModelObject) listAllRequests.getSelectedValue();
                    requestViewer.setMessage(currentlyDisplayedItem.getRequest(), true);
                    responseViewer.setMessage(currentlyDisplayedItem.getResponse(), false);
                }
            }
        });
        JScrollPane list1 = new JScrollPane(listAllRequests);
        splitPane.setLeftComponent(list1);

        JTabbedPane tabs = new JTabbedPane();
        callbacks = BurpExtender.getInstance().getCallbacks();
        requestViewer = callbacks.createMessageEditor(this, true);
        responseViewer = callbacks.createMessageEditor(this, true);
        tabs.addTab("Request", requestViewer.getComponent());
        tabs.addTab("Response", responseViewer.getComponent());
        splitPane.setRightComponent(tabs);

        add(topPanel,BorderLayout.NORTH);
        add(splitPane,BorderLayout.CENTER);
        add(closePanel,BorderLayout.SOUTH);

    }

    @Override
    public IHttpService getHttpService() {
        return currentlyDisplayedItem.getHttpService();    }

    @Override
    public byte[] getRequest() {
        return currentlyDisplayedItem.getRequest();    }

    @Override
    public byte[] getResponse() {
        return currentlyDisplayedItem.getResponse();    }
}
