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
        List<Request> rqs = sequence.getRequest();
        for (Request rq: rqs) {
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
                SqliteHelper db = new SqliteHelper();
                if(name.isEmpty()){
                    JOptionPane.showMessageDialog(DialogSequenceEdit.this,"Sequence name is not set.","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(db.isSequenceNameUsed(name)){
                    JOptionPane.showMessageDialog(DialogSequenceEdit.this,"Sequence Name is Duplicated.","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                db.updateSequenceName(sequence.getId(),name);
                dissmiss();
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
        requestViewer = callbacks.createMessageEditor(this, false);
        responseViewer = callbacks.createMessageEditor(this, false);
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
