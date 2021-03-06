package com.behsazan.view.dialogs;

import com.behsazan.model.adapters.SequenceListModelObject;
import com.behsazan.model.entity.Sequence;
import com.behsazan.model.sqlite.SqliteHelper;
import com.behsazan.view.abstracts.AbstractDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by admin on 08/07/2017.
 */
public class DialogSelectSequence extends AbstractDialog {

    private JPanel buttonsPanel;
    private List<SequenceListModelObject> selectedItem;
    private JList<SequenceListModelObject> listSequences;
    private DefaultListModel<SequenceListModelObject> modelList;

    public DialogSelectSequence() {
        super(false);
    }


    public void setData(){
        java.util.List<Sequence> allSeq = Sequence.getAllSequences();
        for (Sequence seq : allSeq) {
            modelList.addElement(new SequenceListModelObject(seq));
        }
    }

    @Override
    protected void initUI() {
        setSize(300, 500);
        setTitle("Choose A Sequence");
        setLocationRelativeTo(getParentWindow());
        installEscapeCloseOperation();
        setLayout(new BorderLayout());
        add(new JScrollPane(getList()) ,BorderLayout.CENTER);
        add(getButtonsPanel(),BorderLayout.SOUTH);
    }

    public JList getList() {
        if(listSequences == null){
            modelList = new DefaultListModel<>();

            listSequences = new JList<>(modelList);
            listSequences.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(e.getClickCount() == 2){
                        doSelect();
                    }
                }
            });
            listSequences.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }
        return listSequences;
    }

    public JPanel getButtonsPanel() {
        if(buttonsPanel == null){
            buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton newDialog = new JButton("New Sequence");
            newDialog.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DialogSequenceNew dlg = new DialogSequenceNew();
                    dlg.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            modelList.removeAllElements();
                            setData();
                        }
                    });
                }
            });
            JButton btnOk = new JButton("Ok");
            btnOk.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doSelect();
                }
            });
            JButton btnCancel = new JButton("Cancel");
            btnCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dissmiss();
                }
            });

            buttonsPanel.add(newDialog);
            buttonsPanel.add(btnOk);
            buttonsPanel.add(btnCancel);
        }
        return buttonsPanel;
    }

    private void doSelect() {
        selectedItem = listSequences.getSelectedValuesList();
        DialogSelectSequence.this.setVisible(false);
    }

    public List<SequenceListModelObject> getSelectedItem(){
        setVisible(true);
        return selectedItem;
    }

}
