package com.behsazan.view.panels;

import burp.*;
import com.behsazan.model.DataUtils;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.Request;
import com.behsazan.model.entity.Sequence;
import com.behsazan.view.abstracts.AbstractPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Created by admin on 08/01/2017.
 */
public class PanelTestSequencePreTest extends AbstractPanel implements IMessageEditorController {

    private JTextField txtRootAddress;
    private IBurpExtenderCallbacks callbacks;
    private JSplitPane splitPane;
    private IMessageEditor requestViewer;
    private IMessageEditor responseViewer;
    private DefaultListModel<RequestListModelObject> modelAllRequests;
    private JList listAllRequests;
    private RequestListModelObject currentlyDisplayedItem;
    private Sequence sequence;

    @Override
    public String getName() {
        return "PanelTestSequencePreTest";
    }

    public void setData(Sequence sq) {
        this.sequence = sq;
        java.util.List<Request> rqs = sequence.getRequest();
        for (Request rq: rqs) {
            modelAllRequests.addElement(new RequestListModelObject(rq));
        }
        this.listAllRequests.repaint();
        this.txtRootAddress.setText(DataUtils.getRootAddress(sq.getRequest().get(0)));
        this.txtRootAddress.repaint();

    }

    @Override
    protected void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblSeqName = new JLabel("Change URL Root: ");
        txtRootAddress = new JTextField("",60);
        topPanel.add(lblSeqName);
        topPanel.add(txtRootAddress);

        callbacks = BurpExtender.getInstance().getCallbacks();
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
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
                    if(requestViewer.isMessageModified()){
                        currentlyDisplayedItem.setRequest(requestViewer.getMessage());
                    }
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

    public List<byte[]> getEditedRequests(){
        List<byte[]> ret = new ArrayList<>();
        Enumeration<RequestListModelObject> els = modelAllRequests.elements();
        while(els.hasMoreElements()){
            RequestListModelObject el = els.nextElement();
            ret.add(el.getRequest());
        }
        return ret;
    }

    public URL getNewRootURL() throws MalformedURLException {
        return new URL(txtRootAddress.getText());
    }
}
