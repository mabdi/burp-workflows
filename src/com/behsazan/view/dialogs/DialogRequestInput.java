package com.behsazan.view.dialogs;

import com.behsazan.model.entity.Request;
import com.behsazan.model.entity.RequestIn;
import com.behsazan.model.entity.TestCase_Request;
import com.behsazan.view.abstracts.AbstractDialog;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;


/**
 * Created by admin on 08/08/2017.
 */
public class DialogRequestInput extends AbstractDialog{
    private JTextField txtPlaceHolder;
    private JComboBox<String> comboType;
    private JTextArea txtValues;
    private JPanel toolbar;
    private JButton btnAdd;
    private JButton btncancel;
    private RequestIn result;
    private RequestIn requestIn;

    public DialogRequestInput(Component parent) {
        super(parent,false);
    }

    @Override
    protected void initUI() {
        setSize(300,350);
        setTitle("Request Input");
        setLocationRelativeTo(getParentWindow());

        SpringLayout layoutTop = new SpringLayout();
        JPanel topPanel = new JPanel(layoutTop);

        JLabel lblSeqName = new JLabel("Place Holder: ");
        txtPlaceHolder = new JTextField("",18);

        JLabel lblBase = new JLabel("Type: ");
        comboType = new JComboBox<>();
        DefaultComboBoxModel<String> modelCombo = new DefaultComboBoxModel<>();
        Map<Integer, String> kvp = RequestIn.getTypesString();
        for (String v:kvp.values() ) {
            modelCombo.addElement(v);
        }
        comboType.setModel(modelCombo);

        final JButton btnFile = new JButton("Load From Variables");
        comboType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Map<Integer, String> kvp = RequestIn.getTypesString();
                if(comboType.getSelectedItem().equals(kvp.get(RequestIn.TYPE_FROM_LIST))){
                    btnFile.setText("Import From File");
                }
                if(comboType.getSelectedItem().equals(kvp.get(RequestIn.TYPE_LOCAL))){
                    btnFile.setText("Load From Local Variables");
                }
                if(comboType.getSelectedItem().equals(kvp.get(RequestIn.TYPE_GLOBAL))){
                    btnFile.setText("Load From Global Variables");
                }
            }
        });
        JLabel lblCookie = new JLabel("Values: ");
        txtValues = new JTextArea(6,18 );
        btnFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Map<Integer, String> kvp = RequestIn.getTypesString();
                if(comboType.getSelectedItem().equals(kvp.get(RequestIn.TYPE_FROM_LIST))){
                    // open File Chooser
                    final JFileChooser fc = new JFileChooser();
                    int returnVal = fc.showOpenDialog(DialogRequestInput.this);
                    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fc.addChoosableFileFilter(new FileFilter() {
                        @Override
                        public boolean accept(File f) {
                            return f.getAbsolutePath().toLowerCase().endsWith(".txt");
                        }

                        @Override
                        public String getDescription() {
                            return "Text Files (*.txt)";
                        }
                    });
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        try {
                            List<String> lines = FileUtils.readLines(file, Charset.forName("UTF-8"));
                            for (String line :
                                    lines) {
                                appendLine(line);
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                if(comboType.getSelectedItem().equals(kvp.get(RequestIn.TYPE_LOCAL))){
                    DialogSelectVariable dlg = new DialogSelectVariable(DialogRequestInput.this);
                    dlg.setData(false);
                    List<String> items = dlg.getSelectedItem();

                    for (String line :
                            items) {
                        appendLine(line);
                    }
                }
                if(comboType.getSelectedItem().equals(kvp.get(RequestIn.TYPE_GLOBAL))){
                    DialogSelectVariable dlg = new DialogSelectVariable(DialogRequestInput.this);
                    dlg.setData(true);
                    List<String> items = dlg.getSelectedItem();
                    for (String line :
                            items) {
                        appendLine(line);
                    }
                }
            }
        });

        topPanel.add(lblSeqName);
        topPanel.add(txtPlaceHolder);
        topPanel.add(lblBase);
        topPanel.add(comboType);
        topPanel.add(lblCookie);
        topPanel.add(txtValues);
        topPanel.add(btnFile);

        int PADD_WEST = 80;
        int PADD_EAST = -20;

        layoutTop.putConstraint(SpringLayout.WEST,lblSeqName,10,SpringLayout.WEST,topPanel);
        layoutTop.putConstraint(SpringLayout.NORTH,lblSeqName,10,SpringLayout.NORTH,topPanel);
        layoutTop.putConstraint(SpringLayout.WEST,txtPlaceHolder,PADD_WEST,SpringLayout.WEST,topPanel);
        layoutTop.putConstraint(SpringLayout.EAST,txtPlaceHolder,PADD_EAST,SpringLayout.EAST,topPanel);
        layoutTop.putConstraint(SpringLayout.NORTH,txtPlaceHolder,0,SpringLayout.NORTH,lblSeqName);


        layoutTop.putConstraint(SpringLayout.WEST,lblBase,10,SpringLayout.WEST,topPanel);
        layoutTop.putConstraint(SpringLayout.NORTH,lblBase,10,SpringLayout.SOUTH,lblSeqName);
        layoutTop.putConstraint(SpringLayout.WEST,comboType,PADD_WEST,SpringLayout.WEST,topPanel);
        layoutTop.putConstraint(SpringLayout.EAST,comboType,PADD_EAST,SpringLayout.EAST,topPanel);
        layoutTop.putConstraint(SpringLayout.NORTH,comboType,0,SpringLayout.NORTH,lblBase);

        layoutTop.putConstraint(SpringLayout.WEST,lblCookie,10,SpringLayout.WEST,topPanel);
        layoutTop.putConstraint(SpringLayout.NORTH,lblCookie,10,SpringLayout.SOUTH,lblBase);
        layoutTop.putConstraint(SpringLayout.WEST,txtValues,PADD_WEST,SpringLayout.WEST,topPanel);
        layoutTop.putConstraint(SpringLayout.EAST,txtValues,PADD_EAST,SpringLayout.EAST,topPanel);
        layoutTop.putConstraint(SpringLayout.NORTH,txtValues,0,SpringLayout.NORTH,lblCookie);

        layoutTop.putConstraint(SpringLayout.WEST,btnFile,PADD_WEST,SpringLayout.WEST,topPanel);
        layoutTop.putConstraint(SpringLayout.NORTH,btnFile,10,SpringLayout.SOUTH,txtValues);

        setLayout(new BorderLayout());
        add(topPanel,BorderLayout.CENTER);
        add(getToolbar(),BorderLayout.SOUTH);
    }

    private void appendLine(String line){

            txtValues.append(line);
            txtValues.append("\n");

    }

    public Component getToolbar() {
        if(toolbar == null){
            toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnAdd = new JButton("Add");
            btnAdd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    requestIn.setType(getSelectedType());
                    requestIn.setPlaceHoder(txtPlaceHolder.getText());
                    requestIn.setTxtValue(txtValues.getText());
                    result = requestIn;
                    dissmiss();
                }
            });
            btncancel = new JButton("Close");
            btncancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dissmiss();
                }
            });
            toolbar.add(btnAdd);
            toolbar.add(btncancel);
        }
        return toolbar;
    }

    public RequestIn getData(TestCase_Request request) {
        requestIn = new RequestIn(-1, getSelectedType(), txtPlaceHolder.getText(),txtValues.getText() );
        initData();
        setVisible(true);
        return result;
    }

    public RequestIn getEditData(RequestIn requestIn){
        this.requestIn = requestIn;
        initData(requestIn);
        setVisible(true);
        return result;
    }

    private void initData(RequestIn requestIn) {
        txtValues.setText(requestIn.getTxtValue());
        txtPlaceHolder.setText(requestIn.getPlaceHoder());
        String selected = RequestIn.getTypesString().get(requestIn.getType());
        comboType.setSelectedItem(selected);
    }

    private void initData() {
        comboType.setSelectedIndex(0);
    }

    public int getSelectedType() {
        String selected = (String) comboType.getSelectedItem();
        Set<Map.Entry<Integer, String>> kvp = RequestIn.getTypesString().entrySet();
        for (Map.Entry<Integer,String> kv:kvp) {
            if(kv.getValue().equals(selected)){
                return kv.getKey();
            }
        }
        return RequestIn.TYPE_FROM_LIST;
    }
}
