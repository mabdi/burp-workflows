package com.behsazan.view.panels;

import burp.*;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.Request;
import com.behsazan.view.UIUtils;
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
    private JList listAllRequests;
    private BurpExtender ext;
    private DefaultListModel<RequestListModelObject> modelAllRequests;
    private DefaultListModel<RequestListModelObject> modelDoRequests;
    private DefaultListModel<RequestListModelObject> modelSelectedRequests;
    private JList listSelectedReqs;
    private JTextField txtSeqName;
    private JCheckBox enableFilter;
    private JTextField txtUrl;

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
        JPanel topPanel = new JPanel(new GridBagLayout());
        UIUtils.FormUtility form = new UIUtils.FormUtility();
        txtSeqName = new JTextField("", 20);
        txtUrl = new JTextField("", 20);


        form.addLabel("Sequence Name: ",topPanel);
        form.addLastField(txtSeqName,topPanel);

        form.addLabel("Base URL: ",topPanel);
        form.addLastField(txtUrl,topPanel);

        enableFilter = new JCheckBox("Show Only .do requests");
        enableFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (enableFilter.isSelected()) {
                    listAllRequests.setModel(modelDoRequests);
                } else {
                    listAllRequests.setModel(modelAllRequests);
                }
            }
        });
        form.addLabel("",topPanel);
        form.addLastField(enableFilter,topPanel);


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
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnBar.add(startRecord);
        btnBar.add(stopRecord);
        form.addLabel("",topPanel);
        form.addLastField(btnBar,topPanel);
//        topPanel.add(txtSeqName);
//        topPanel.add(startRecord);
//        topPanel.add(stopRecord);
//        topPanel.add(enableFilter);




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

        requestViewer = callbacks.createMessageEditor(this, true);
        responseViewer = callbacks.createMessageEditor(this, false);
        tabs.addTab("Request", requestViewer.getComponent());
        tabs.addTab("Response", responseViewer.getComponent());
        splitPane.setRightComponent(tabs);
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    @Override
    public IHttpService getHttpService() {
        return currentlyDisplayedItem.getHttpService();
    }

    @Override
    public byte[] getRequest() {
        return currentlyDisplayedItem.getRequest();
    }

    @Override
    public byte[] getResponse() {
        return currentlyDisplayedItem.getResponse();
    }

    public Component getListAll() {
        modelAllRequests = new DefaultListModel<>();
        modelDoRequests = new DefaultListModel<>();
        listAllRequests = new JList(modelAllRequests);
        listAllRequests.setVisibleRowCount(10);
        listAllRequests.setFixedCellHeight(20);
        listAllRequests.setFixedCellWidth(140);
        listAllRequests.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listAllRequests.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    currentlyDisplayedItem = (RequestListModelObject) listAllRequests.getSelectedValue();
                    requestViewer.setMessage(currentlyDisplayedItem.getRequest(), true);
                    responseViewer.setMessage(currentlyDisplayedItem.getResponse(), false);
                }

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
                DefaultListModel<RequestListModelObject> model;
                if (enableFilter.isSelected()) {
                    model = modelDoRequests;
                } else {
                    model = modelAllRequests;
                }
                modelSelectedRequests.addElement(model.elementAt(listAllRequests.getSelectedIndex()));
            }
        });
        buttonPanel.add(buttonin);

        JButton buttonout = new JButton("<<");
        buttonout.setToolTipText("Deselect");
        buttonout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modelSelectedRequests.removeElementAt(listSelectedReqs.getSelectedIndex());
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
                if (e.getValueIsAdjusting()) {
                    currentlyDisplayedItem = (RequestListModelObject) listSelectedReqs.getSelectedValue();
                    requestViewer.setMessage(currentlyDisplayedItem.getRequest(), true);
                    responseViewer.setMessage(currentlyDisplayedItem.getResponse(), false);
                }
            }
        });
        listSelectedReqs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane list2 = new JScrollPane(listSelectedReqs);
        return list2;
    }

    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) throws Exception {
        if (!messageIsRequest) {
            RequestListModelObject req = new RequestListModelObject(message);
            addMessage(req);
        }
    }

    public void addMessage(RequestListModelObject req) {
        synchronized (modelAllRequests) {
            modelAllRequests.addElement(req);
            if (req.getAnalysed().getUrl().getPath().endsWith(".do")) {
                modelDoRequests.addElement(req);
            }
        }
    }

    public String getSequenceName() {
        return txtSeqName.getText();
    }

    public List<Request> getSelectedRequests() {
        ArrayList<Request> list = new ArrayList<>();
        Enumeration<RequestListModelObject> els = modelSelectedRequests.elements();
        int rid = 0;
        while (els.hasMoreElements()) {
            RequestListModelObject rq = els.nextElement();
            list.add(new Request(rq.getAnalysed().getUrl(), rq.getRequest(), rq.getResponse(), rid));
            rid++;
        }
        return list;
    }
}
