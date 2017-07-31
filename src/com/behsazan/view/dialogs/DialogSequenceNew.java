package com.behsazan.view.dialogs;

import burp.BurpExtender;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.view.abstracts.AbstractDialog;
import com.behsazan.view.panels.PanelEditSequence;
import com.behsazan.view.panels.PanelNewSequenceChooseRequests;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Created by admin on 07/29/2017.
 */
public class DialogSequenceNew extends AbstractDialog {
    private JPanel toolbar;
    private JPanel cardPanel;
    private PanelNewSequenceChooseRequests choosePanel;
    private PanelEditSequence editPanel;
    private CardLayout cardLayout;
    private JButton btnnext;
    private JButton btncancel;
    private JButton btnfinish;
    private JButton btnprev;

    public DialogSequenceNew(JPanel parent) {
        super(parent);
    }

    @Override
    protected void initUI() {
        setSize(800,600);
        setTitle("New Sequence");
        setLocationRelativeTo(getParentWindow());
        BurpExtender.getInstance().getStdout().println("new dialog initing");
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        choosePanel = new PanelNewSequenceChooseRequests();
        editPanel = new PanelEditSequence();
        cardPanel.add(choosePanel.getName(),choosePanel);
        cardPanel.add(editPanel.getName(),editPanel);
        add(cardPanel, BorderLayout.CENTER);
        add(getToolbar(), BorderLayout.SOUTH);
        btnfinish.setEnabled(false);
        btnprev.setEnabled(false);
        cardLayout.first(cardPanel);
        BurpExtender.getInstance().getStdout().println("new dialog done");
    }

    public Component getToolbar() {
        if(toolbar == null){
            toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnprev = new JButton("Previous");
            btnprev.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cardLayout.previous(cardPanel);
                    btnnext.setEnabled(true);
                    btnprev.setEnabled(false);
                    btnfinish.setEnabled(false);
                }
            });
            btnnext = new JButton("Next");
            btnnext.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
//            editPanel.setEntity(en)
                    cardLayout.next(cardPanel);
                    btnnext.setEnabled(false);
                    btnprev.setEnabled(true);
                    btnfinish.setEnabled(true);

                    List<RequestListModelObject> reqs = choosePanel.getSelectedRequests();
                    editPanel.setRequets(reqs);
                }
            });
            btnfinish = new JButton("Finish");
            btnfinish.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // save entity
                }
            });
            btncancel = new JButton("Cancel");
            btncancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    choosePanel.shutDown();
                    dissmiss();
                }
            });
            toolbar.add(btnprev);
            toolbar.add(btnnext);
            toolbar.add(btnfinish);
            toolbar.add(btncancel);
        }
        return toolbar;
    }
}
