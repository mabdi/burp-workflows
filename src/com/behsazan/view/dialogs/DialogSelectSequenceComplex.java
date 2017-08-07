package com.behsazan.view.dialogs;

import burp.*;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.Request;
import com.behsazan.model.entity.Sequence;
import com.behsazan.model.sqlite.SqliteHelper;
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
 * Created by admin on 08/07/2017.
 */
public class DialogSelectSequenceComplex extends AbstractDialog implements IMessageEditorController {

    private JPanel topPanel;
    private JSplitPane centerPanel;
    private JPanel buttonsPanel;
    private JComboBox<Sequence> sequencesCombo;
    private DefaultComboBoxModel<Sequence> modelCombo;
    private JTabbedPane tabs;
    private JList<RequestListModelObject> listSequences;
    private DefaultListModel<RequestListModelObject> modelList;
    private IMessageEditor requestViewer;
    private IMessageEditor responseViewer;
    private RequestListModelObject currentlyDisplayedItem;
    private Sequence selectedItem;

    public DialogSelectSequenceComplex(Component parent) {
        super(parent);
    }

    @Override
    protected void initUI() {
        setSize(600, 500);
        setTitle("Choose A Sequence");
        setLocationRelativeTo(getParentWindow());

        setLayout(new BorderLayout());
        add(getTopPanel(), BorderLayout.NORTH);
        add(getCenterPanel(), BorderLayout.CENTER);
        add(getButtonsPanel(), BorderLayout.SOUTH);

    }

    public JPanel getTopPanel() {
        if(topPanel == null){
            topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            modelCombo = new DefaultComboBoxModel<Sequence>();
            List<Sequence> allSeq = new SqliteHelper().getAllSequences();
            for (Sequence seq :
                    allSeq) {
                modelCombo.addElement(seq);
            }
            sequencesCombo = new  JComboBox<>(modelCombo);
            sequencesCombo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Sequence s = (Sequence) sequencesCombo.getSelectedItem();
                    modelList.clear();
                    for (Request r:s.getRequest()) {
                        modelList.addElement(new RequestListModelObject(r));
                    }
                }
            });
            topPanel.add(sequencesCombo);
        }
        return topPanel;
    }

    public JSplitPane getCenterPanel() {
        if (centerPanel == null) {
            centerPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            centerPanel.setRightComponent(getTabs());
            centerPanel.setLeftComponent(getList());
            centerPanel.setResizeWeight(0.2);
        }
        return centerPanel;
    }

    public JPanel getButtonsPanel() {
        if(buttonsPanel == null){
            buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnOk = new JButton("Ok");
            btnOk.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectedItem = (Sequence) sequencesCombo.getSelectedItem();
                    dissmiss();
                }
            });
            JButton btnCancel = new JButton("Cancel");
            btnCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dissmiss();
                }
            });
            buttonsPanel.add(btnOk);
            buttonsPanel.add(btnCancel);
        }
        return buttonsPanel;
    }

    public JTabbedPane getTabs() {
        if(tabs == null){
            tabs = new JTabbedPane();
            IBurpExtenderCallbacks callbacks = BurpExtender.getInstance().getCallbacks();
            requestViewer = callbacks.createMessageEditor(this, false);
            responseViewer = callbacks.createMessageEditor(this, false);
            tabs.addTab("Request", requestViewer.getComponent());
            tabs.addTab("Response", responseViewer.getComponent());
        }
        return tabs;
    }

    public JList getList() {
        if(listSequences == null){
            modelList = new DefaultListModel<RequestListModelObject>();
            listSequences = new JList<RequestListModelObject>();
            listSequences.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    currentlyDisplayedItem = (RequestListModelObject) listSequences.getSelectedValue();
                    requestViewer.setMessage(currentlyDisplayedItem.getRequest(), true);
                    responseViewer.setMessage(currentlyDisplayedItem.getResponse(), false);
                }
            });
        }
        return listSequences;
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

    public Sequence getSelectedItem(){
        return selectedItem;
    }

}
