package com.behsazan.view.dialogs;

import burp.BurpExtender;
import com.behsazan.model.DataUtils;
import com.behsazan.model.adapters.SequenceListModelObject;
import com.behsazan.model.entity.Request;
import com.behsazan.model.entity.TestCase;
import com.behsazan.model.entity.TestCase_Sequence;
import com.behsazan.model.sqlite.SqliteHelper;
import com.behsazan.view.abstracts.AbstractDialog;
import com.behsazan.view.abstracts.AbstractTab;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.*;
import java.util.List;

/**
 * Created by admin on 08/02/2017.
 */
public class DialogTestCaseNew extends AbstractDialog {

    private JPanel topPanel;
    private JSplitPane centerSplitPanel;
    private JPanel buttonsPanel;
    private JTextField txtTestCaseName;
    private JPanel sequenceJListPanel;
    private JPanel sequenceDetailPanel;
    private JList<SequenceListModelObject> sequncesJlist;
    private JTextField txtRootAddress;
    private JTextField txtBase1;
    private JTextField txtBase2;
    private JTextField txtCookie;
    private JPanel centerPanel;
    private DefaultListModel<SequenceListModelObject> modelSequeces;
    private JButton btnRequest;
    private SequenceListModelObject activeSequence;

    public DialogTestCaseNew(AbstractTab tabTestCases) {
        super(tabTestCases);

    }

    @Override
    protected void initUI() {
        setSize(800,600);
        setTitle("New TestCase");
        setLocationRelativeTo(getParentWindow());

        setLayout(new BorderLayout());
        add(getTopPanel(), BorderLayout.NORTH);
        add(getCenterSplitPanel(), BorderLayout.CENTER);
        add(getButtonsPanel(), BorderLayout.SOUTH);
    }

    public JPanel getTopPanel() {
        if (topPanel == null) {
            topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.add(new JLabel("TestCase Name: "));
            topPanel.add(getTxtTestCaseName());
        }
        return topPanel;
    }

    public JPanel getCenterSplitPanel() {
        if (centerPanel == null) {
            centerPanel = new JPanel(new BorderLayout());
            centerSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            centerSplitPanel.setDividerLocation(0.2);
            centerSplitPanel.setLeftComponent(getSequenceJListPanel());
            centerSplitPanel.setRightComponent(getSequenceDetailPanel());

            centerPanel.add(centerSplitPanel,BorderLayout.CENTER);
        }
        return centerPanel;
    }

    public JPanel getButtonsPanel() {
        if(buttonsPanel==null){
            buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dissmiss();
                }
            });
            JButton addBtn = new JButton("Save");
            addBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SqliteHelper db = new SqliteHelper();
                    List<SequenceListModelObject> reqs = getSelectedSequences();
                    String name = txtTestCaseName.getText();
                    if(name.isEmpty() ){
                        JOptionPane.showMessageDialog(DialogTestCaseNew.this,"Some required filed is not set.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if(reqs.size()==0){
                        JOptionPane.showMessageDialog(DialogTestCaseNew.this,"No sequence is added.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if(db.isSequenceNameUsed(name)){
                        JOptionPane.showMessageDialog(DialogTestCaseNew.this,"Testcase Name is Duplicated.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try {
                        List<TestCase_Sequence> ts = new ArrayList<>();
                        for (SequenceListModelObject req: reqs) {
                            ts.add(req.getTestCase_sequence());
                        }
                        db.insertTestCase(new TestCase(name ,ts));
                        dissmiss();

                    }catch (Exception x){
                        BurpExtender.getInstance().getStdout().println("save Error "+x.getMessage() + "\n");
                        x.printStackTrace(BurpExtender.getInstance().getStdout());
                    }
                }
            });
            buttonsPanel.add(cancelBtn);
            buttonsPanel.add(addBtn);
        }
        return buttonsPanel;
    }

    public JTextField getTxtTestCaseName() {
        if(txtTestCaseName == null){
            txtTestCaseName = new JTextField("",20);
        }
        return txtTestCaseName;
    }

    public JPanel getSequenceJListPanel() {
        if(sequenceJListPanel == null){
            sequenceJListPanel = new JPanel(new BorderLayout());
            modelSequeces = new DefaultListModel<>();
            sequncesJlist = new JList(modelSequeces);
            sequncesJlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            sequncesJlist.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if(!validateUpdate()){
                        sequncesJlist.setSelectedValue(activeSequence,true);
                    }
                    updateSequenceDetail();
                    activeSequence = sequncesJlist.getSelectedValue();
                    showSequenceDetail();

                }
            });
            JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton addSequence = new JButton("+");
            addSequence.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    final DialogSelectSequence dlg = new DialogSelectSequence(DialogTestCaseNew.this);
                    dlg.setData();
                    addNewSequence(dlg.getSelectedItem());
                    dlg.dissmiss();
                }
            });
            JButton removeSequence = new JButton("-");
            removeSequence.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(sequncesJlist.getSelectedIndex()<0){
                        return;
                    }
                    int rsp = JOptionPane.showConfirmDialog(DialogTestCaseNew.this,"Are you sure to delete?","Delete",JOptionPane.YES_NO_OPTION);
                    if(rsp == JOptionPane.YES_OPTION){
                        int sel = sequncesJlist.getSelectedIndex();
                        sequncesJlist.setSelectedIndex(0);
                        modelSequeces.remove(sel);
                    }
                }
            });
            btns.add(addSequence);
            btns.add(removeSequence);
            sequenceJListPanel.add(new JLabel("Sequence order: "),BorderLayout.NORTH);
            sequenceJListPanel.add(new JScrollPane(sequncesJlist),BorderLayout.CENTER);
            sequenceJListPanel.add(btns,BorderLayout.SOUTH);
        }
        return sequenceJListPanel;
    }

    private boolean validateUpdate() {
        if(activeSequence==null)
            return false;
        try {
            new URL(txtRootAddress.getText());

        } catch (MalformedURLException e) {
            JOptionPane.showMessageDialog(this,"URL format is not correct.","Error",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(txtBase1.getText().isEmpty() || txtBase2.getText().isEmpty() || txtRootAddress.getText().isEmpty()){
            JOptionPane.showMessageDialog(DialogTestCaseNew.this,"Some required filed is not set.","Error",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return  true;
    }

    private void updateSequenceDetail() {
        if(activeSequence==null)
            return;
        try {
            activeSequence.getTestCase_sequence().setUrl(new URL(txtRootAddress.getText()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        activeSequence.getTestCase_sequence().setBase1(txtBase1.getText());
        activeSequence.getTestCase_sequence().setBase2(txtBase2.getText());
        activeSequence.getTestCase_sequence().setCookie(txtCookie.getText());
    }

    private void showSequenceDetail() {
        Request req1 = activeSequence.getSequence().getRequest().get(0);
        txtRootAddress.setText(DataUtils.getRootAddress(req1));
        txtRootAddress.setEnabled(true);
        txtBase1.setEnabled(true);
        txtBase1.setText(DataUtils.getBasePath(req1));
        txtBase2.setEnabled(true);
        txtBase2.setText(DataUtils.getBasePath(req1));
        txtCookie.setEnabled(true);
        txtCookie.setText(DataUtils.getCookie(req1));
        btnRequest.setEnabled(true);
    }

    private void addNewSequence(List<SequenceListModelObject> selectedItem) {
        if(selectedItem!=null) {
            for (SequenceListModelObject s:
                    selectedItem) {
                modelSequeces.addElement(s);
                sequncesJlist.setSelectedValue(s,true);
            }
            ParseAndshowWaitDialog();
        }
    }

    private void ParseAndshowWaitDialog() {
        final JDialog pleaseWaitDialog = new JDialog(this);
        JPanel panel = new JPanel();
        final JLabel dialogWaitlabel = new JLabel("Please wait...");
        panel.add(dialogWaitlabel );
        pleaseWaitDialog.add(panel);
        pleaseWaitDialog.setTitle("Please wait...");
        pleaseWaitDialog.setModalityType(ModalityType.APPLICATION_MODAL);
        pleaseWaitDialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        pleaseWaitDialog.pack();
        pleaseWaitDialog.setLocationRelativeTo(this);
        final SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {

            @Override
            protected Void doInBackground() throws Exception {
                Enumeration<SequenceListModelObject> els = modelSequeces.elements();
                while(els.hasMoreElements()){
                    parse(els.nextElement());
                }
                return null;
            }

            private void parse(SequenceListModelObject sequenceListModelObject) {
                if(sequenceListModelObject.getTestCase_sequence()!=null){
                    return;
                }
                publish("Parsing " + sequenceListModelObject.getSequence().getName());
                sequenceListModelObject.setTestCase_sequence(TestCase_Sequence.initBySequence(sequenceListModelObject.getSequence()));
            }

            @Override
            protected void process(List<String> chunks) {
                dialogWaitlabel.setText(chunks.get(0));
                pleaseWaitDialog.pack();
                pleaseWaitDialog.setLocationRelativeTo(DialogTestCaseNew.this);
                pleaseWaitDialog.repaint();
            }

            @Override
            protected void done() {
                pleaseWaitDialog.dispose();
            }
        };
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                worker.execute();
            }
        });
        pleaseWaitDialog.setVisible(true);
    }

    public JPanel getSequenceDetailPanel() {
        if(sequenceDetailPanel == null){
            SpringLayout spring = new SpringLayout();
            sequenceDetailPanel = new JPanel(spring);
            JLabel lblSeqName = new JLabel("Change URL Root: ");
            txtRootAddress = new JTextField("",42);
            txtRootAddress.setEnabled(false);

            JLabel lblBase = new JLabel("Change Path Base: ");
            txtBase1 = new JTextField("",20);
            txtBase1.setEnabled(false);
            txtBase2 = new JTextField("",20);
            txtBase2.setEnabled(false);

            JLabel lblCookie = new JLabel("Change Cookie: ");
            txtCookie = new JTextField("",42);
            txtCookie.setEnabled(false);

            JLabel lblRequests = new JLabel("Edit Requests: ");
            btnRequest = new JButton("Edit Requests");
            btnRequest.setEnabled(false);
            btnRequest.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DialogTestCaseRequests dlg = new DialogTestCaseRequests(DialogTestCaseNew.this);
                    TestCase_Sequence data = dlg.setData(sequncesJlist.getSelectedValue().getTestCase_sequence());
                    if(data!=null){
                        sequncesJlist.getSelectedValue().setTestCase_sequence(data);
                    }
                }
            });

            sequenceDetailPanel.add(lblSeqName);
            sequenceDetailPanel.add(txtRootAddress);
            sequenceDetailPanel.add(lblBase);
            sequenceDetailPanel.add(txtBase1);
            sequenceDetailPanel.add(txtBase2);
            sequenceDetailPanel.add(lblCookie);
            sequenceDetailPanel.add(txtCookie);
            sequenceDetailPanel.add(lblRequests);
            sequenceDetailPanel.add(btnRequest);


            spring.putConstraint(SpringLayout.WEST,lblSeqName,10,SpringLayout.WEST,sequenceDetailPanel);
            spring.putConstraint(SpringLayout.NORTH,lblSeqName,10,SpringLayout.NORTH,sequenceDetailPanel);
            spring.putConstraint(SpringLayout.WEST,txtRootAddress,140,SpringLayout.WEST,sequenceDetailPanel);
            spring.putConstraint(SpringLayout.NORTH,txtRootAddress,0,SpringLayout.NORTH,lblSeqName);

            spring.putConstraint(SpringLayout.WEST,lblBase,10,SpringLayout.WEST,sequenceDetailPanel);
            spring.putConstraint(SpringLayout.NORTH,lblBase,10,SpringLayout.SOUTH,lblSeqName);
            spring.putConstraint(SpringLayout.WEST,txtBase1,140,SpringLayout.WEST,sequenceDetailPanel);
            spring.putConstraint(SpringLayout.NORTH,txtBase1,0,SpringLayout.NORTH,lblBase);
            spring.putConstraint(SpringLayout.WEST,txtBase2,5,SpringLayout.EAST,txtBase1);
            spring.putConstraint(SpringLayout.NORTH,txtBase2,0,SpringLayout.NORTH,lblBase);

            spring.putConstraint(SpringLayout.WEST,lblCookie,10,SpringLayout.WEST,sequenceDetailPanel);
            spring.putConstraint(SpringLayout.NORTH,lblCookie,10,SpringLayout.SOUTH,lblBase);
            spring.putConstraint(SpringLayout.WEST,txtCookie,140,SpringLayout.WEST,sequenceDetailPanel);
            spring.putConstraint(SpringLayout.NORTH,txtCookie,0,SpringLayout.NORTH,lblCookie);


            spring.putConstraint(SpringLayout.WEST,lblRequests,10,SpringLayout.WEST,sequenceDetailPanel);
            spring.putConstraint(SpringLayout.NORTH,lblRequests,10,SpringLayout.SOUTH,lblCookie);
            spring.putConstraint(SpringLayout.WEST,btnRequest,140,SpringLayout.WEST,sequenceDetailPanel);
            spring.putConstraint(SpringLayout.NORTH,btnRequest,0,SpringLayout.NORTH,lblRequests);


        }
        return sequenceDetailPanel;
    }

    private List<SequenceListModelObject> getSelectedSequences(){
        ArrayList<SequenceListModelObject> list = new ArrayList<>();
        Enumeration<SequenceListModelObject> els = modelSequeces.elements();
        int rid = 0;
        while(els.hasMoreElements()){
            SequenceListModelObject rq = els.nextElement();
            list.add(rq);
            rid++;
        }
        return list;
    }

}
