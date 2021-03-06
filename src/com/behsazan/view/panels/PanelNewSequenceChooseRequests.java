package com.behsazan.view.panels;

import burp.*;
import com.behsazan.model.DataUtils;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.Request;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
    private JTextField txtDescription;

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
        UIUtils.FormUtility form = new UIUtils.FormUtility(topPanel);
        txtSeqName = new JTextField("", 20);
        txtSeqName.requestFocus();
        txtSeqName.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());
        txtUrl = new JTextField("", 20);
        txtUrl.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());
        txtDescription = new JTextField("", 20);
        txtDescription.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());


        form.addLabel("Sequence Name: " );
        form.addLastField(txtSeqName);

        form.addLabel("Description: ");
        form.addLastField(txtDescription);

        form.addLabel("Base URL: ");

        form.addMiddleField(txtUrl);
        JPanel jpnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton jbtn = new JButton("Try to set");
        jbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtUrl.setText(getReferedURL());
            }
        });
        jpnl.add(jbtn);
        form.addLastField(jpnl);

        enableFilter = new JCheckBox("Apply filter");
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
//        form.addLabel("",topPanel);
//        form.addLastField(enableFilter,topPanel);


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
        btnBar.add(enableFilter);
        form.addLabel("");
        form.addLastField(btnBar);
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
        if(currentlyDisplayedItem==null)
            return null;
        return currentlyDisplayedItem.getHttpService();
    }

    @Override
    public byte[] getRequest() {
        if(currentlyDisplayedItem ==null)
            return null;
        return currentlyDisplayedItem.getRequest();
    }

    public byte[] getRequestModified() {
        if(currentlyDisplayedItem ==null)
            return null;
        return requestViewer.getMessage();
    }

    @Override
    public byte[] getResponse() {
        if(currentlyDisplayedItem ==null)
            return null;
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
            if (!DataUtils.isInFilter(req.getAnalysed().getUrl())) {
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
            list.add(new Request(rq.getHttpService(), rq.getRequest(), rq.getResponse(), rid));
            rid++;
        }
        return list;
    }

    public String getSequenceDescription() {
        return txtDescription.getText();
    }

    public String getSequenceURL() {
        return txtUrl.getText();
    }

    public String getReferedURL() {
        List<Request> rqs = getSelectedRequests();
//        String path = rq.getAnalysedRequest().getUrl().getPath();
        String url = DataUtils.getRootAddress(rqs.get(0));
        for(Request rq : rqs){
            if(!url.equals(  DataUtils.getRootAddress(rq))){
                return "";
            }
        }
        String pttStr = rqs.get(0).getAnalysedRequest().getUrl().getPath();
        if (pttStr.trim().isEmpty()) {
            return url;
        }
        String[] ptt = pttStr.substring(1).split("/");
        if (ptt.length == 0) {
            return url;
        }
        for(Request rq : rqs){
            String pttStr2 = rq.getAnalysedRequest().getUrl().getPath();
            String[] ptt2 = pttStr2.substring(1).split("/");
            for (int i = 0; i < ptt.length && i < ptt2.length; i++) {
                if(!ptt[i].equals(ptt2[i])){
                    ptt[i] = "";
                }
            }
        }
        String path = "";
        for (int i = 0; i < ptt.length; i++) {
            String p = ptt[i];
            if(p.trim().isEmpty()){
                    break;
            }
            path += "/" + p;
        }
        return url + path;
    }
}
