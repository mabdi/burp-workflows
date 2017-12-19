package com.behsazan.view.dialogs;

import burp.BurpExtender;
import burp.IHttpService;
import com.behsazan.controller.Controller;
import com.behsazan.model.DataUtils;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.Request;
import com.behsazan.model.entity.Sequence;
import com.behsazan.model.settings.Settings;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractDialog;
import com.behsazan.view.panels.PanelNewSequenceChooseRequests;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by admin on 07/29/2017.
 */
public class DialogSequenceNew extends AbstractDialog {
    private JPanel toolbar;
    private JPanel cardPanel;
    private PanelNewSequenceChooseRequests choosePanel;
    private CardLayout cardLayout;
    private JButton btncancel;
    private JButton btnfinish;
    private JButton btnReSend;

    public DialogSequenceNew() {
        super();
    }

    @Override
    protected void initUI() {
        setSize(800,750);
        setTitle("New Sequence");
        setLocationRelativeTo(getParentWindow());
        installEscapeCloseOperation();
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
            btnReSend = new JButton("Resend");
            btnReSend.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    final byte[] rq = choosePanel.getRequestModified();
                    if(rq== null)
                        return;
                    final IHttpService service = choosePanel.getHttpService();
                    btnReSend.setEnabled(false);
                    SwingWorker<RequestListModelObject,Void> worker = new SwingWorker<RequestListModelObject, Void>() {
                        @Override
                        protected RequestListModelObject doInBackground() throws Exception {

                            return Controller.makeHttpRequest(service, rq);
                        }

                        @Override
                        protected void done() {
                            try {
                                choosePanel.addMessage(get());
                                btnReSend.setEnabled(true);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            } catch (ExecutionException e1) {
                                e1.printStackTrace();
                            }
                        }
                    };
                    worker.execute();
                }
            });
            btnfinish = new JButton("Save");
            btnfinish.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    List<Request> reqs = choosePanel.getSelectedRequests();
                    String name = choosePanel.getSequenceName().trim();
                    String description = choosePanel.getSequenceDescription().trim();
                    String url = choosePanel.getSequenceURL().trim();
                    if(name.isEmpty()){
                        JOptionPane.showMessageDialog(DialogSequenceNew.this,"Sequence name is not set.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if(reqs.size()==0){
                        JOptionPane.showMessageDialog(DialogSequenceNew.this,"No request is selected.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if(   Sequence.isSequenceNameUsed(name)){
                        JOptionPane.showMessageDialog(DialogSequenceNew.this,"Sequence Name is Duplicated.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if(url.isEmpty()){
                        JOptionPane.showMessageDialog(DialogSequenceNew.this, "Url is not set.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if(!DataUtils.isValidURL(url)){
                        JOptionPane.showMessageDialog(DialogSequenceNew.this,"Invalid Url.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    boolean isNew = true;
                    for(String bu: Settings.getBaseUrls()){
                        if(bu.equals(url)){
                            isNew = false;
                        }
                    }
                    if(isNew){
                        int resp = JOptionPane.showConfirmDialog(DialogSequenceNew.this, url+"\nThe Url is not exists in Base Urls.\nDo you want to add ?", "Base Url", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if(resp == JOptionPane.YES_OPTION){
                            Settings.addBaseUrl(url);
                        }
                    }
                    try {
                        Sequence.insertSequence(new Sequence(name, description, url, reqs));
                        choosePanel.shutDown();
                        dissmiss();

                    }catch (Exception x){
                        BurpExtender.getInstance().getStdout().println("save Error "+x.getMessage() + "\n");
                        x.printStackTrace(BurpExtender.getInstance().getStdout());
                        UIUtils.showGenerealError();
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
            toolbar.add(btnReSend);
            toolbar.add(btnfinish);
            toolbar.add(btncancel);
        }
        return toolbar;
    }
}
