package com.behsazan.view.tabs;

import com.behsazan.controller.Flow_Running;
import com.behsazan.view.abstracts.AbstractTab;

import javax.swing.*;
import java.awt.*;

/**
 * Created by admin on 07/29/2017.
 */
public class TabGlobals extends AbstractTab {

    private JTable jtableGlobals;
    // TODO, add new global variable
    // TODO, edit global variable value

    @Override
    public String getTabTitle() {
        return "Globals";
    }

    @Override
    protected void initUI() {
        setLayout(new BorderLayout());
        add(new JScrollPane(getJtableGlobals()), BorderLayout.CENTER);
    }


    public JTable getJtableGlobals() {
        if (jtableGlobals == null) {
            jtableGlobals = new JTable();
            jtableGlobals.setModel(Flow_Running.globalsToTableModel());
        }
        return jtableGlobals;
    }
}
