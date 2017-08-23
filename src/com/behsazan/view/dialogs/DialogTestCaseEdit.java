package com.behsazan.view.dialogs;

import burp.BurpExtender;
import com.behsazan.model.DataUtils;
import com.behsazan.model.adapters.SequenceListModelObject;
import com.behsazan.model.entity.*;
import com.behsazan.model.sqlite.SqliteHelper;
import com.behsazan.view.UIUtils;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Created by admin on 08/02/2017.
 */
public class DialogTestCaseEdit extends AbstractDialog {

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
    private JPanel centerPanel;
    private DefaultListModel<SequenceListModelObject> modelSequeces;
    private JButton btnRequest;
    private SequenceListModelObject activeSequence;
    private TestCase testCase;
    private JComboBox<String> cmbCookie;
    private DefaultComboBoxModel<String> modelCookie;
    private Vector<Vector<Object>> vectorCookie;

    public DialogTestCaseEdit(AbstractTab tabTestCases) {
        super(tabTestCases,false);
    }

    public void initData(final int testCaseid) {
        final DialogWaiting pleaseWaitDialog = DialogWaiting.showWaitingDialog(this);
        final SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {

            @Override
            protected Void doInBackground() throws Exception {
                testCase = TestCase.getById(testCaseid);
                for (TestCase_Sequence seq: testCase.getSeqs()) {
                    SequenceListModelObject s = new SequenceListModelObject(seq.getSequence());
                    s.setTestCase_sequence(seq);
                    modelSequeces.addElement(s);
                }
                return null;
            }


            @Override
            protected void done() {
                pleaseWaitDialog.dispose();
            }
        };
        worker.execute();
        pleaseWaitDialog.setVisible(true);
        txtTestCaseName.setText(testCase.getName());
        setVisible(true);
    }

    @Override
    protected void initUI() {
        setSize(800,600);
        setTitle("Edit TestCase");
        setLocationRelativeTo(getParentWindow());

        setLayout(new BorderLayout());
        add(getTopPanel(), BorderLayout.NORTH);
        add(getCenterSplitPanel(), BorderLayout.CENTER);
        add(getButtonsPanel(), BorderLayout.SOUTH);
    }

    public JPanel getTopPanel() {
        if (topPanel == null) {
            topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//            JButton btnUpdateName = new JButton("Update Name");
//            btnUpdateName.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    String name = getTxtTestCaseName().getText();
//                    if(name.isEmpty()){
//                        JOptionPane.showMessageDialog(DialogTestCaseEdit.this,"TestCase name is not set.","Error",JOptionPane.ERROR_MESSAGE);
//                        return;
//                    }
//                    if(TestCase.isTestCaseNameUsed(name)){
//                        JOptionPane.showMessageDialog(DialogTestCaseEdit.this,"TestCase Name is Duplicated.","Error",JOptionPane.ERROR_MESSAGE);
//                        return;
//                    }
//                    try {
//                        Sequence.updateSequenceName(testCase.getId(),name);
//                    } catch (SQLException e1) {
//                        e1.printStackTrace();
//                        UIUtils.showGenerealError(DialogTestCaseEdit.this);
//                    }
//                }
//            });
            topPanel.add(new JLabel("TestCase Name: "));
            topPanel.add(getTxtTestCaseName());
//            topPanel.add(btnUpdateName);
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
            JButton addBtn = new JButton("Update");
            addBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    List<SequenceListModelObject> reqs = getSelectedSequences();
                    String name = txtTestCaseName.getText();
                    if(name.isEmpty() ){
                        JOptionPane.showMessageDialog(DialogTestCaseEdit.this,"Some required filed is not set.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if(reqs.size()==0){
                        JOptionPane.showMessageDialog(DialogTestCaseEdit.this,"No sequence is added.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String description = "";
                    try {
                        List<TestCase_Sequence> ts = new ArrayList<>();
                        for (SequenceListModelObject req: reqs) {
                            ts.add(req.getTestCase_sequence());
                        }
                        testCase.setSeqs(ts);
                        testCase.setName(name);
                        testCase.setDescription(description);
                        TestCase.updateTestCase(testCase);
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
                    final DialogSelectSequence dlg = new DialogSelectSequence(DialogTestCaseEdit.this);
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
                    int rsp = JOptionPane.showConfirmDialog(DialogTestCaseEdit.this,"Are you sure to delete?","Delete",JOptionPane.YES_NO_OPTION);
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
            JOptionPane.showMessageDialog(this,"Some required filed is not set.","Error",JOptionPane.ERROR_MESSAGE);
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
        if(cmbCookie.getSelectedIndex()>0) {
            String param = (String) vectorCookie.get(cmbCookie.getSelectedIndex() - 1).get(3);
            activeSequence.getTestCase_sequence().setCookie(param);
        }else{
            activeSequence.getTestCase_sequence().setCookie("");
        }
    }

    private void showSequenceDetail() {
        Request req1 = activeSequence.getSequence().getRequest().get(0);
        txtRootAddress.setText(DataUtils.getRootAddress(req1));
        txtRootAddress.setEnabled(true);
        txtBase1.setEnabled(true);
        txtBase1.setText(DataUtils.getBasePath(req1));
        txtBase2.setEnabled(true);
        txtBase2.setText(DataUtils.getBasePath(req1));
        cmbCookie.setEnabled(true);
        String cookeOutParam = activeSequence.getTestCase_sequence().getCookie();
        cmbCookie.setSelectedIndex(0);
        if(!cookeOutParam.isEmpty()){
            int i=1;
            for(Vector<Object> obj: vectorCookie){
                if(obj.get(3).equals(cookeOutParam)){
                    cmbCookie.setSelectedIndex(i);
                    break;
                }
                i++;
            }
        }
        btnRequest.setEnabled(true);
    }

    private void addNewSequence(List<SequenceListModelObject> selectedItem) {
        if(selectedItem!=null) {
            for (SequenceListModelObject s:
                    selectedItem) {
                modelSequeces.addElement(s);
            }
            parseAndshowWaitDialog();
        }
    }

    private void parseAndshowWaitDialog(final DialogWaiting pleaseWaitDialog) {
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
                DialogWaiting.updateMessage(pleaseWaitDialog,chunks.get(0));
            }

            @Override
            protected void done() {
                pleaseWaitDialog.dispose();
            }
        };
        worker.execute();
    }

    private void parseAndshowWaitDialog() {
        final DialogWaiting pleaseWaitDialog = DialogWaiting.showWaitingDialog(this);
        parseAndshowWaitDialog(pleaseWaitDialog);
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
            modelCookie = new DefaultComboBoxModel<String>();
            vectorCookie = Login.getAllLogins_Table();
            modelCookie.addElement("none");
            for(Vector<Object> login: vectorCookie){
                modelCookie.addElement(""+ login.get(0) + ". " + login.get(1) + " (" + login.get(3) + ")");
            }
            cmbCookie = new JComboBox<String>(modelCookie);
            cmbCookie.setEnabled(false);

            JLabel lblRequests = new JLabel("Edit Requests: ");
            btnRequest = new JButton("Edit Requests");
            btnRequest.setEnabled(false);
            btnRequest.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DialogTestCaseRequests dlg = new DialogTestCaseRequests(DialogTestCaseEdit.this);
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
            sequenceDetailPanel.add(cmbCookie);
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
            spring.putConstraint(SpringLayout.WEST,cmbCookie,140,SpringLayout.WEST,sequenceDetailPanel);
            spring.putConstraint(SpringLayout.NORTH,cmbCookie,0,SpringLayout.NORTH,lblCookie);


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
