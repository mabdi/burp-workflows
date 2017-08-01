package com.behsazan.view.dialogs;

import burp.BurpExtender;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.Request;
import com.behsazan.model.entity.Sequence;
import com.behsazan.model.sqlite.SqliteHelper;
import com.behsazan.view.abstracts.AbstractDialog;
import com.behsazan.view.panels.PanelEditSequence;
import com.behsazan.view.panels.PanelNewSequenceChooseRequests;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JButton btncancel;
    private JButton btnfinish;

    public DialogSequenceNew(JPanel parent) {
        super(parent);
    }

    @Override
    protected void initUI() {
        setSize(800,600);
        setTitle("New Sequence");
        setLocationRelativeTo(getParentWindow());
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        choosePanel = new PanelNewSequenceChooseRequests();
        cardPanel.add(choosePanel.getName(),choosePanel);
        add(cardPanel, BorderLayout.CENTER);
        add(getToolbar(), BorderLayout.SOUTH);
        cardLayout.first(cardPanel);
    }

    public Component getToolbar() {
        if(toolbar == null){
            toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnfinish = new JButton("Save");
            btnfinish.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SqliteHelper db = new SqliteHelper();
                    List<Request> reqs = choosePanel.getSelectedRequests();
                    String name = choosePanel.getSequenceName().trim();
                    if(name.isEmpty()){
                        JOptionPane.showMessageDialog(DialogSequenceNew.this,"Sequence name is not set.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if(reqs.size()==0){
                        JOptionPane.showMessageDialog(DialogSequenceNew.this,"No request is selected.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if(db.isSequenceNameUsed(name)){
                        JOptionPane.showMessageDialog(DialogSequenceNew.this,"Sequence Name is Duplicated.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try {
                        db.insertSequence(new Sequence(name,reqs));
                        choosePanel.shutDown();
                        dissmiss();

                    }catch (Exception x){
                        BurpExtender.getInstance().getStdout().println("save Error "+x.getMessage() + "\n");
                        x.printStackTrace(BurpExtender.getInstance().getStdout());
                    }

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
            toolbar.add(btnfinish);
            toolbar.add(btncancel);
        }
        return toolbar;
    }
}
