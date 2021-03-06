package com.behsazan.view.panels;

import burp.*;
import com.behsazan.controller.Controller;
import com.behsazan.model.DataUtils;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.view.abstracts.AbstractPanel;
import com.behsazan.view.dialogs.OnSequencePlayFinished;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;

/**
 * Created by admin on 08/01/2017.
 */
public class PanelSequencePlay extends AbstractPanel implements IMessageEditorController {
    private OnSequencePlayFinished onFinish;
    private boolean forceCancel = false;
    private IBurpExtenderCallbacks callbacks;
    private JSplitPane splitPane;
    private DefaultListModel<RequestListModelObject> modelAllRequests;
    private JList listAllRequests;
    private RequestListModelObject currentlyDisplayedItem;
    private IMessageEditor requestViewer;
    private IMessageEditor responseViewer;
    private List<byte[]> requests;
    private URL newRoot;
    private BurpExtender ext;
    private JButton cancelPlay;

    @Override
    protected void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblSeqName = new JLabel("Progress: ");

        cancelPlay = new JButton("Cancel");
        cancelPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                forceCancel = true;
            }
        });
        topPanel.add(lblSeqName);
        topPanel.add(cancelPlay);

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
                    currentlyDisplayedItem = (RequestListModelObject) listAllRequests.getSelectedValue();
                    requestViewer.setMessage(currentlyDisplayedItem.getRequest(), false);
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

    public void setData(URL newroot, List<byte[]> reqs) {
        ext = BurpExtender.getInstance();
        this.newRoot = newroot;
        this.requests = reqs;
    }

    public void play(OnSequencePlayFinished onFinish) {
        this.onFinish = onFinish;
        play();
    }

    private void play() {
        this.cancelPlay.setEnabled(true);
        SwingWorker<Void,RequestListModelObject> worker = new SwingWorker<Void, RequestListModelObject>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (final byte[] rq :
                        requests) {
                    if(forceCancel){
                        break;
                    }
                    publish(Controller.makeHttpRequestAndWait(newRoot, rq));
                }
                return null;
            }

            @Override
            protected void done() {
                cancelPlay.setEnabled(false);
                forceCancel=false;
                if(onFinish!= null){
                    onFinish.testFinished();
                }
            }

            @Override
            protected void process(List<RequestListModelObject> chunks) {
                for (RequestListModelObject rq : chunks) {
                    modelAllRequests.addElement(rq);
                }

            }
        };
        worker.execute();
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
