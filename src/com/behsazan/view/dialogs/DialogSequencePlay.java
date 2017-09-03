package com.behsazan.view.dialogs;

import com.behsazan.model.entity.Sequence;
import com.behsazan.view.abstracts.AbstractDialog;
import com.behsazan.view.panels.PanelSequencePlay;
import com.behsazan.view.panels.PanelTestSequencePreTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by admin on 08/01/2017.
 */
public class DialogSequencePlay extends AbstractDialog implements OnSequencePlayFinished {


    private CardLayout cardLayout;
    private JPanel cardPanel;
    private PanelTestSequencePreTest prePanel;
    private JPanel toolbar;
    private JButton btnPlay;
    private JButton btncancel;
    private PanelSequencePlay playPanel;
    private Sequence sequence;

    public DialogSequencePlay(int id) {
        super(false);
        setSequence(id);
        setVisible(true);
    }

    private void setSequence(int id) {
        this.sequence = Sequence.getById(id);
        setData();
    }

    private void setData() {
        prePanel.setData(sequence);
    }

    @Override
    protected void initUI() {
        setSize(800,600);
        setTitle("Play/Test Sequence");
        setLocationRelativeTo(getParentWindow());
        installEscapeCloseOperation();
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        prePanel = new PanelTestSequencePreTest();
        cardPanel.add(prePanel.getName(),prePanel);
        playPanel = new PanelSequencePlay();
        cardPanel.add(playPanel.getName(),playPanel);
        add(cardPanel, BorderLayout.CENTER);
        add(getToolbar(), BorderLayout.SOUTH);
        cardLayout.first(cardPanel);
    }

    public Component getToolbar() {
        if(toolbar == null){
            toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnPlay = new JButton("Play");
            btnPlay.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    URL newroot = null;
                    try {
                        newroot = prePanel.getNewRootURL();
                    } catch (MalformedURLException e1) {
                        JOptionPane.showMessageDialog(getParentWindow(),"URL format is not correct.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    List<byte[]> reqs = prePanel.getEditedRequests();
                    btnPlay.setEnabled(false); // set true when an Error occurred or play done.
                    playPanel.setData(newroot, reqs);
                    playPanel.play(DialogSequencePlay.this);
                    cardLayout.next(cardPanel);
                }
            });
            btncancel = new JButton("Close");
            btncancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prePanel.shutDown();
                    playPanel.shutDown();
                    dissmiss();
                }
            });
            toolbar.add(btnPlay);
            toolbar.add(btncancel);
        }
        return toolbar;
    }

    @Override
    public void testFinished() {
//        btnPlay.setEnabled(true);
    }

}
