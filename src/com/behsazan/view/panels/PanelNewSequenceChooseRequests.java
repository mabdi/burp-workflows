package com.behsazan.view.panels;

import burp.*;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.view.abstracts.AbstractPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


/**
 * Created by admin on 07/29/2017.
 */
public class PanelNewSequenceChooseRequests extends AbstractPanel implements IMessageEditorController, IProxyListener {
    private JSplitPane splitPane;
    private IBurpExtenderCallbacks callbacks;
    private IMessageEditor requestViewer;
    private IMessageEditor responseViewer;
    private RequestListModelObject currentlyDisplayedItem;
    private Component listAll;
    private Component buttons;
    private Component listSelected;
    private JList listAllRequests;
    private BurpExtender ext;
    private DefaultListModel<RequestListModelObject> modelAllRequests;
    private DefaultListModel<RequestListModelObject> modelSelectedRequests;
    private JList listSelectedReqs;
    private JTextField txtSeqName;

    @Override
    public String getName() {
        return "PanelNewSequenceChooseRequests";
    }

    public PanelNewSequenceChooseRequests() {
        super();
        ext = BurpExtender.getInstance();
    }

    @Override
    public void shutDown() {
        super.shutDown();
        ext.getCallbacks().removeProxyListener(PanelNewSequenceChooseRequests.this);
    }

    @Override
    protected void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblSeqName = new JLabel("Sequence Name: ");
        txtSeqName = new JTextField("",20);
        final JButton startRecord = new JButton("Record");
        final JButton stopRecord = new JButton("Stop");
        startRecord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ext.getCallbacks().registerProxyListener(PanelNewSequenceChooseRequests.this);
                startRecord.setEnabled(false);
                stopRecord.setEnabled(true);
            }
        });

        stopRecord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ext.getCallbacks().removeProxyListener(PanelNewSequenceChooseRequests.this);
                startRecord.setEnabled(true);
                stopRecord.setEnabled(false);
            }
        });
        stopRecord.setEnabled(false);
        topPanel.add(lblSeqName);
        topPanel.add(txtSeqName);
        topPanel.add(startRecord);
        topPanel.add(stopRecord);

        callbacks = BurpExtender.getInstance().getCallbacks();
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(0.5);
        JPanel selectPanel = new JPanel();
        selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.LINE_AXIS));
        selectPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        selectPanel.add(getListAll());
        selectPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        selectPanel.add(getButtons());
        selectPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        selectPanel.add(getListSelected());
        selectPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        splitPane.setLeftComponent(selectPanel);
        JTabbedPane tabs = new JTabbedPane();

        requestViewer = callbacks.createMessageEditor(this, false);
        responseViewer = callbacks.createMessageEditor(this, false);
        tabs.addTab("Request", requestViewer.getComponent());
        tabs.addTab("Response", responseViewer.getComponent());
        splitPane.setRightComponent(tabs);
        setLayout(new BorderLayout());
        add(topPanel,BorderLayout.NORTH);
        add(splitPane,BorderLayout.CENTER);
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

    public Component getListAll() {
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
                responseViewer.setMessage(currentlyDisplayedItem.getResponse(), false);

            }
        });
        JScrollPane list1 = new JScrollPane(listAllRequests);

        return list1;
    }

    public Component getButtons() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton buttonin = new JButton(">>");
        buttonin.setToolTipText("Select");
        buttonin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 modelSelectedRequests.addElement(modelAllRequests.elementAt( listAllRequests.getSelectedIndex() ));
            }
        });
        buttonPanel.add(buttonin);

        JButton buttonout = new JButton("<<");
        buttonout.setToolTipText("Deselect");
        buttonout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modelSelectedRequests.removeElementAt(listSelectedReqs.getSelectedIndex() );
            }
        });
        buttonPanel.add(buttonout);

        return buttonPanel;
    }

    public Component getListSelected() {
        modelSelectedRequests = new DefaultListModel();
        listSelectedReqs = new JList(modelSelectedRequests);
        listSelectedReqs.setVisibleRowCount(10);
        listSelectedReqs.setFixedCellHeight(20);
        listSelectedReqs.setFixedCellWidth(140);
        listSelectedReqs.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                currentlyDisplayedItem = modelSelectedRequests.elementAt(e.getFirstIndex());
                requestViewer.setMessage(currentlyDisplayedItem.getRequest(), true);
                responseViewer.setMessage(currentlyDisplayedItem.getResponse(), false);

            }
        });
        listSelectedReqs.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane list2 = new JScrollPane(listSelectedReqs);
        return list2;
    }

    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) throws Exception {
        if (!messageIsRequest)
        {
            synchronized(modelAllRequests)
            {
                int row = modelAllRequests.size();
                RequestListModelObject req = new RequestListModelObject(message);
                modelAllRequests.addElement(req);

            }
        }
    }

    public String getSequenceName(){
        return txtSeqName.getText();
    }

    public List<RequestListModelObject> getSelectedRequests(){
        ArrayList<RequestListModelObject> list = new ArrayList<>();
        Enumeration<RequestListModelObject> els = modelSelectedRequests.elements();
        while(els.hasMoreElements()){
            list.add(els.nextElement());
        }
        return list;
    }
}
