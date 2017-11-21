package com.behsazan.view.dialogs;

import burp.*;
import com.behsazan.model.DataUtils;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.Request;
import com.behsazan.model.entity.Sequence;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by admin on 07/29/2017.
 */
public class DialogSequenceEdit extends AbstractDialog implements IMessageEditorController {


    private Sequence sequence;
    private IBurpExtenderCallbacks callbacks;
    private JTextField txtSeqName;
    private IMessageEditor requestViewer;
    private IMessageEditor responseViewer;
    private DefaultListModel<RequestListModelObject> modelAllRequests;
    private JList listAllRequests;
    private RequestListModelObject currentlyDisplayedItem;
    private JTextField txtUrl;
    private JTextField txtDescription;

    public DialogSequenceEdit(int id) {
        super(false);
        setSequence(id);
        setVisible(true);
    }

    private void setSequence(int id) {
        this.sequence = Sequence.getById(id);
        setData();
    }

    private void setData() {
        this.txtSeqName.setText(sequence.getName());
        this.txtDescription.setText(sequence.getDescription());
        this.txtUrl.setText(sequence.getUrl());

        this.txtSeqName.repaint();
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
        installEscapeCloseOperation();
        setLayout(new BorderLayout());
//        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//
//        topPanel.add(new JLabel("Sequence Name: "));
//        txtSeqName = new JTextField("",40);
//        topPanel.add(txtSeqName);
        JPanel topPanel = new JPanel(new GridBagLayout());
        UIUtils.FormUtility form = new UIUtils.FormUtility(topPanel);
        txtSeqName = new JTextField("", 20);
        txtSeqName.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());
        txtUrl = new JTextField("", 20);
        txtUrl.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());
        txtDescription = new JTextField("", 20);
        txtDescription.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());


        form.addLabel("Sequence Name: ");
        form.addLastField(txtSeqName);

        form.addLabel("Description: ");
        form.addLastField(txtDescription);

        form.addLabel("Base URL: ");
        form.addLastField(txtUrl);








        JButton updateName = new JButton("Update Info");
        updateName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*
                List<Request> reqs = choosePanel.getSelectedRequests();
                    String name = choosePanel.getSequenceName().trim();
                    String description = choosePanel.getSequenceDescription().trim();
                    String url = choosePanel.getSequenceURL().trim();
                    if(name.isEmpty()){
                        JOptionPane.showMessageDialog(DialogSequenceNew.this,"Sequence name is not set.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if(reqs.size()==0){
                        JOptionPane.showMessageDialog(DialogSequenceNew.this,"No request is selected.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if(Sequence.isSequenceNameUsed(name)){
                        JOptionPane.showMessageDialog(DialogSequenceNew.this,"Sequence Name is Duplicated.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if(!DataUtils.isValidURL(url)){
                        JOptionPane.showMessageDialog(DialogSequenceNew.this,"Invalid Url.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try {
                        Sequence.insertSequence(new Sequence(name, description, url, reqs));
                        choosePanel.shutDown();
                        dissmiss();

                    }catch (Exception x){
                        BurpExtender.getInstance().getStdout().println("save Error "+x.getMessage() + "\n");
                        x.printStackTrace(BurpExtender.getInstance().getStdout());
                        UIUtils.showGenerealError();
                    }
                 */


                String name = txtSeqName.getText().trim();
                String description = txtDescription.getText().trim();
                String url = txtUrl.getText().trim();

                if(name.isEmpty()){
                    JOptionPane.showMessageDialog(DialogSequenceEdit.this,"Sequence name is not set.","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(!sequence.getName().equals(name) && Sequence.isSequenceNameUsed(name)){
                    JOptionPane.showMessageDialog(DialogSequenceEdit.this,"Sequence Name is Duplicated.","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(!DataUtils.isValidURL(url)){
                    JOptionPane.showMessageDialog(DialogSequenceEdit.this,"Invalid Url.","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    Sequence.updateSequence(sequence.getId(),name,description,url);
                    dissmiss();
                } catch (SQLException e1) {
                    UIUtils.showGenerealError( );
                }
            }
        });
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
                    UIUtils.showGenerealError( );
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
                    UIUtils.showGenerealError( );
                }

            }
        });
        closePanel.add(updateName);
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
