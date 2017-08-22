package com.behsazan.view.dialogs;

import com.behsazan.model.entity.TestCase_Sequence;
import com.behsazan.view.abstracts.AbstractDialog;
import com.behsazan.view.panels.PanelTestCaseRequests;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by admin on 08/07/2017.
 */
public class DialogTestCaseRequests extends AbstractDialog {
    private TestCase_Sequence sequence;
    private TestCase_Sequence result;
    private JPanel buttonsPanel;
    private PanelTestCaseRequests requestsPanel;

    public DialogTestCaseRequests(Component parent) {
        super(parent,false);
    }

    public TestCase_Sequence setData(TestCase_Sequence sequence){
        this.sequence = sequence;
        requestsPanel.setData(sequence);
        setVisible(true);
        return result;
    }

    @Override
    protected void initUI() {
        setSize(1000, 700);
        setTitle("TestCase Sequence Requests");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(getParentWindow());
        requestsPanel = new PanelTestCaseRequests();
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
                    result = null;
                    requestsPanel.updateRequestDetail();
                    dissmiss();
                }
            });
            buttonsPanel.add(cancelBtn);
        }
        return buttonsPanel;
    }
}
