package com.behsazan.view.panels;

import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.view.abstracts.AbstractPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by admin on 07/29/2017.
 */
public class PanelEditSequence extends AbstractPanel {

    private List<RequestListModelObject> requets;
    private JSplitPane splitPanel1;
    private JSplitPane splitPanel2;
    private JPanel listPanel;

    @Override
    public String getName() {
        return "PanelEditSequence";
    }

    @Override
    protected void initUI() {
        splitPanel1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPanel2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        listPanel = new JPanel();
        listPanel.setLayout(new GridBagLayout());

        splitPanel1.setLeftComponent(listPanel);
        splitPanel1.setRightComponent(splitPanel2);
        setLayout(new BorderLayout());
        add(splitPanel1,BorderLayout.CENTER);
    }

    public void setRequets(List<RequestListModelObject> requets) {
        this.requets = requets;
    }
}
