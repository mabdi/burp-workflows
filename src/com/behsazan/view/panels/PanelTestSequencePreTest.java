package com.behsazan.view.panels;

import burp.*;
import com.behsazan.model.DataUtils;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.Request;
import com.behsazan.model.entity.Sequence;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractPanel;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private JTextField txtCookie;
    private JTextField txtBase1;
    private JTextField txtBase2;

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
        this.txtCookie.setText(DataUtils.getCookie(sq.getRequest().get(0)));
        this.txtBase1.setText(DataUtils.getBasePath(sq.getRequest().get(0)));
        this.txtBase2.setText(DataUtils.getBasePath(sq.getRequest().get(0)));
        this.txtRootAddress.repaint();

    }

    @Override
    protected void initUI() {
        SpringLayout layoutTop = new SpringLayout();
        JPanel topPanel = new JPanel(layoutTop);
        topPanel.setPreferredSize(new Dimension(400,100));
        JLabel lblSeqName = new JLabel("Change URL Root: ");
        txtRootAddress = new JTextField("",60);
        txtRootAddress.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());

        JLabel lblBase = new JLabel("Change Path Base: ");
        txtBase1 = new JTextField("",30);
        txtBase1.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());
        txtBase2 = new JTextField("",30);
        txtBase2.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());

        JLabel lblCookie = new JLabel("Change Cookie: ");
        txtCookie = new JTextField("",60);
        txtCookie.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());


        topPanel.add(lblSeqName);
        topPanel.add(txtRootAddress);
        topPanel.add(lblBase);
        topPanel.add(txtBase1);
        topPanel.add(txtBase2);
        topPanel.add(lblCookie);
        topPanel.add(txtCookie);


        layoutTop.putConstraint(SpringLayout.WEST,lblSeqName,10,SpringLayout.WEST,topPanel);
        layoutTop.putConstraint(SpringLayout.NORTH,lblSeqName,10,SpringLayout.NORTH,topPanel);
        layoutTop.putConstraint(SpringLayout.WEST,txtRootAddress,140,SpringLayout.WEST,topPanel);
        layoutTop.putConstraint(SpringLayout.NORTH,txtRootAddress,0,SpringLayout.NORTH,lblSeqName);

        layoutTop.putConstraint(SpringLayout.WEST,lblBase,10,SpringLayout.WEST,topPanel);
        layoutTop.putConstraint(SpringLayout.NORTH,lblBase,10,SpringLayout.SOUTH,lblSeqName);
        layoutTop.putConstraint(SpringLayout.WEST,txtBase1,140,SpringLayout.WEST,topPanel);
        layoutTop.putConstraint(SpringLayout.NORTH,txtBase1,0,SpringLayout.NORTH,lblBase);
        layoutTop.putConstraint(SpringLayout.WEST,txtBase2,5,SpringLayout.EAST,txtBase1);
        layoutTop.putConstraint(SpringLayout.NORTH,txtBase2,0,SpringLayout.NORTH,lblBase);

        layoutTop.putConstraint(SpringLayout.WEST,lblCookie,10,SpringLayout.WEST,topPanel);
        layoutTop.putConstraint(SpringLayout.NORTH,lblCookie,10,SpringLayout.SOUTH,lblBase);
        layoutTop.putConstraint(SpringLayout.WEST,txtCookie,140,SpringLayout.WEST,topPanel);
        layoutTop.putConstraint(SpringLayout.NORTH,txtCookie,0,SpringLayout.NORTH,lblCookie);

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
            String[] msg = DataUtils.ExplodeRequest(el.getRequest());
            msg = DataUtils.changeCookie(msg,txtCookie.getText());
            msg = DataUtils.changeHost(msg,txtRootAddress.getText());
            msg = DataUtils.changeReferer(msg,txtRootAddress.getText());
            msg = DataUtils.changeUrlBase(msg,txtBase1.getText().trim(),txtBase2.getText().trim());
            ret.add(DataUtils.buildRequest(msg));
        }
        return ret;
    }

    public URL getNewRootURL() throws MalformedURLException {
        return new URL(txtRootAddress.getText());
    }

}
