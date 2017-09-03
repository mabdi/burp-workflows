package com.behsazan.view.dialogs;

import com.behsazan.model.entity.Flow_Sequence;
import com.behsazan.view.abstracts.AbstractDialog;
import com.behsazan.view.panels.PanelFlowRequests;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by admin on 08/07/2017.
 */
public class DialogFlowRequests extends AbstractDialog {
    private Flow_Sequence sequence;
    private Flow_Sequence result;
    private JPanel buttonsPanel;
    private PanelFlowRequests requestsPanel;

    public DialogFlowRequests() {
        super(false);
    }

    public Flow_Sequence setData(Flow_Sequence sequence){
        this.sequence = sequence;
        requestsPanel.setData(sequence);
        setVisible(true);
        return result;
    }

    @Override
    protected void initUI() {
        setSize(1000, 700);
        setTitle("Parametric Sequence Requests");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        installEscapeCloseOperation();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                closing();
            }
        });
        setLocationRelativeTo(getParentWindow());
        requestsPanel = new PanelFlowRequests();
        setLayout(new BorderLayout());
        add(requestsPanel, BorderLayout.CENTER);
        add(getButtonsPanel(), BorderLayout.SOUTH);

    }

    public JPanel getButtonsPanel() {
        if(buttonsPanel==null){
            buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancelBtn = new JButton("Close");
            cancelBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    closing();
                }
            });
            buttonsPanel.add(cancelBtn);
        }
        return buttonsPanel;
    }

    private void closing() {
        result = null;
        requestsPanel.updateRequestDetail();
        dissmiss();
    }
}
