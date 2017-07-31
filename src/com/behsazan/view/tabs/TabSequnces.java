package com.behsazan.view.tabs;

import burp.BurpExtender;
import com.behsazan.view.abstracts.AbstractTab;
import com.behsazan.view.dialogs.DialogSequenceNew;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by admin on 07/29/2017.
 */
public class TabSequnces extends AbstractTab {

    private JPanel toolbar;
    private JTable table;
    private Component tableScroll;

    @Override
    protected void initUI() {
        setLayout(new BorderLayout());
        add(getToolbar(), BorderLayout.NORTH);
//        add(getTable(), BorderLayout.CENTER);
    }

    @Override
    public String getTabTitle() {
        return "Sequences";
    }

    public Component getToolbar() {
        if(toolbar == null){
            toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton newNoCookie = new JButton("New");
            newNoCookie.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new DialogSequenceNew(TabSequnces.this);
                }
            });
            JButton editView = new JButton("Edit/View");
            editView.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int tableSelectedRow = table.getSelectedRow();
                }
            });
            JButton delete = new JButton("Delete");
            delete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int tableSelectedRow = table.getSelectedRow();
                }
            });
            JButton playTest = new JButton("Play/Test");
            playTest.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int tableSelectedRow = table.getSelectedRow();
                }
            });
            toolbar.add(newNoCookie);
            toolbar.add(editView);
            toolbar.add(delete);
            toolbar.add(playTest);
            BurpExtender.getInstance().getStdout().println("jtoolbar made.");
        }
        return toolbar;
    }

    public Component getTable() {
        if(tableScroll == null){
//            table = new JTable();
//            table.setModel(null); @TODO -- make a MODEL class in model package
//            tableScroll = new JScrollPane(table);
        }
        return tableScroll;
    }

}
