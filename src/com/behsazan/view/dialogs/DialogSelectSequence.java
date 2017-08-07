package com.behsazan.view.dialogs;

import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.adapters.SequenceListModelObject;
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

/**
 * Created by admin on 08/07/2017.
 */
public class DialogSelectSequence extends AbstractDialog {

    private JPanel buttonsPanel;
    private SequenceListModelObject selectedItem;
    private JList<SequenceListModelObject> listSequences;
    private DefaultListModel<SequenceListModelObject> modelList;

    public DialogSelectSequence(Component parent) {
        super(parent,false);
    }

    @Override
    protected void initUI() {
        setSize(300, 500);
        setTitle("Choose A Sequence");
        setLocationRelativeTo(getParentWindow());
        setLayout(new BorderLayout());
        add(getList(),BorderLayout.CENTER);
        add(getButtonsPanel(),BorderLayout.SOUTH);
    }

    public JList getList() {
        if(listSequences == null){
            modelList = new DefaultListModel<>();
            java.util.List<Sequence> allSeq = new SqliteHelper().getAllSequences();
            for (Sequence seq :
                    allSeq) {
                modelList.addElement(new SequenceListModelObject(seq) );
            }
            listSequences = new JList<>(modelList);
            listSequences.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        return listSequences;
    }

    public JPanel getButtonsPanel() {
        if(buttonsPanel == null){
            buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnOk = new JButton("Ok");
            btnOk.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectedItem = listSequences.getSelectedValue();
                    DialogSelectSequence.this.setVisible(false);
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

    public SequenceListModelObject getSelectedItem(){
        setVisible(true);
        return selectedItem;
    }

}
