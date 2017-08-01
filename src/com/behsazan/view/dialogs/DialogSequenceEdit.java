package com.behsazan.view.dialogs;

import burp.*;
import com.behsazan.model.entity.Request;
import com.behsazan.model.entity.Sequence;
import com.behsazan.view.abstracts.AbstractDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by admin on 07/29/2017.
 */
public class DialogSequenceEdit extends AbstractDialog implements IMessageEditorController {


    private final Sequence sequence;
    private final IBurpExtenderCallbacks callbacks;
    private JTextField sqName;
    private IMessageEditor requestViewer;
    private IMessageEditor responseViewer;
    private DefaultListModel<Request> modelAllRequests;
    private JList listAllRequests;
    private Request currentlyDisplayedItem;

    public DialogSequenceEdit(JPanel parent, int id) {
        super(parent);
        this.sequence = new Sequence(id);
        callbacks = BurpExtender.getInstance().getCallbacks();
        setData();
    }

    private void setData() {

    }

    @Override
    protected void initUI() {
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Sequence Name: "));
        sqName = new JTextField("",40);
        topPanel.add(sqName);
        JButton updateName = new JButton("Update Name");
        updateName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

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

        JPanel selectPanel = new JPanel();
        modelAllRequests = new DefaultListModel<>();
        listAllRequests = new JList(modelAllRequests);
        listAllRequests.setVisibleRowCount(10);
        listAllRequests.setFixedCellHeight(20);
        listAllRequests.setFixedCellWidth(140);
        listAllRequests.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listAllRequests.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                currentlyDisplayedItem = modelAllRequests.elementAt(e.getFirstIndex());
                requestViewer.setMessage(currentlyDisplayedItem.getRequest(), true);
//                responseViewer.setMessage(currentlyDisplayedItem.getResponse(), false);

            }
        });
        JScrollPane list1 = new JScrollPane(listAllRequests);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(0.5);
        JTabbedPane tabs = new JTabbedPane();

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
}
