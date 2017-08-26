package com.behsazan.view.dialogs;

import burp.BurpExtender;
import com.behsazan.model.DataUtils;
import com.behsazan.model.adapters.SequenceListModelObject;
import com.behsazan.model.entity.*;
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
import java.util.*;
import java.util.List;

/**
 * Created by admin on 08/02/2017.
 */
public class DialogFlowNew extends AbstractDialog {

    private JPanel topPanel;
    private JSplitPane centerSplitPanel;
    private JPanel buttonsPanel;
    private JTextField txtflowName;
    private JPanel sequenceJListPanel;
    private JPanel sequenceDetailPanel;
    private JList<SequenceListModelObject> sequncesJlist;
    private JTextField txtRootAddress;
    private JTextField txtBase1;
    private JTextField txtBase2;
    private JComboBox<String> cmbCookie;
    private JPanel centerPanel;
    private DefaultListModel<SequenceListModelObject> modelSequeces;
    private JButton btnRequest;
    private SequenceListModelObject activeSequence;
    private DefaultComboBoxModel<String> modelCookie;
    private Vector<Vector<Object>> vectorCookie;
    private JTextArea txtParam;

    public DialogFlowNew() {
        super();

    }

    @Override
    protected void initUI() {
        setSize(800,600);
        setTitle("New Flow");
        setLocationRelativeTo(getParentWindow());

        setLayout(new BorderLayout());
        add(getTopPanel(), BorderLayout.NORTH);
        add(getCenterSplitPanel(), BorderLayout.CENTER);
        add(getButtonsPanel(), BorderLayout.SOUTH);
    }

    public JPanel getTopPanel() {
        if (topPanel == null) {
//            topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//            topPanel.add(new JLabel("FLOW Name: "));
//            topPanel.add(getTxtFlowName());
            topPanel = new JPanel(new GridBagLayout());
            UIUtils.FormUtility form = new UIUtils.FormUtility();
            form.addLabel("Name :", topPanel);
            form.addLastField(getTxtflowName(), topPanel);

            form.addLabel("Parameters (comma separated):", topPanel);
            txtParam = new JTextArea(2,10);
            form.addLastField(new JScrollPane(txtParam), topPanel);

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
                    List<SequenceListModelObject> reqs = getSelectedSequences();
                    String name = txtflowName.getText();
                    if(name.isEmpty() ){
                        JOptionPane.showMessageDialog(DialogFlowNew.this,"Some required filed is not set.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if(reqs.size()==0){
                        JOptionPane.showMessageDialog(DialogFlowNew.this,"No sequence is added.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if(Flow.isFlowNameUsed(name)){
                        JOptionPane.showMessageDialog(DialogFlowNew.this,"Flow Name is Duplicated.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!validateUpdate()) {
                        JOptionPane.showMessageDialog(DialogFlowNew.this,"Invalid Data.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    updateSequenceDetail();
                    String description = "";
                    try {
                        List<Flow_Sequence> ts = new ArrayList<>();
                        for (SequenceListModelObject req: reqs) {
                            ts.add(req.getFlow_sequence());
                        }
                        Flow.insertFlow(new Flow(name, description ,ts));
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

    public JTextField getTxtflowName() {
        if(txtflowName == null){
            txtflowName = new JTextField("",20);
        }
        return txtflowName;
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

                        if (!validateUpdate()) {
                            sequncesJlist.setSelectedValue(activeSequence, true);
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
                    final DialogSelectSequence dlg = new DialogSelectSequence();
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
                    int rsp = JOptionPane.showConfirmDialog(DialogFlowNew.this,"Are you sure to delete?","Delete",JOptionPane.YES_NO_OPTION);
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
            JOptionPane.showMessageDialog(DialogFlowNew.this,"Some required filed is not set.","Error",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return  true;
    }

    private void updateSequenceDetail() {
        if(activeSequence==null)
            return;
        try {
            activeSequence.getFlow_sequence().setUrl(new URL(txtRootAddress.getText()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        activeSequence.getFlow_sequence().setBase1(txtBase1.getText());
        activeSequence.getFlow_sequence().setBase2(txtBase2.getText());
        if(cmbCookie.getSelectedIndex()>0) {
            String param = (String) vectorCookie.get(cmbCookie.getSelectedIndex() - 1).get(3);
            activeSequence.getFlow_sequence().setCookie(param);
        }else{
            activeSequence.getFlow_sequence().setCookie("");
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
        String cookeOutParam = activeSequence.getFlow_sequence().getCookie();
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
        final SwingWorker<Void, SequenceListModelObject> worker = new SwingWorker<Void, SequenceListModelObject>() {

            @Override
            protected Void doInBackground() throws Exception {
                Enumeration<SequenceListModelObject> els = modelSequeces.elements();
                while(els.hasMoreElements()){
                    parse(els.nextElement());
                }
                return null;
            }

            private void parse(SequenceListModelObject sequenceListModelObject) {
                if(sequenceListModelObject.getFlow_sequence()!=null){
                    return;
                }
                publish(sequenceListModelObject);
                sequenceListModelObject.setFlow_sequence(Flow_Sequence.initBySequence(sequenceListModelObject.getSequence()));
            }

            @Override
            protected void process(List<SequenceListModelObject> chunks) {

                String msg = "Parsing " + chunks.get(0).getSequence().getName();
                dialogWaitlabel.setText(msg);
                pleaseWaitDialog.pack();
                pleaseWaitDialog.setLocationRelativeTo(DialogFlowNew.this);
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
                    DialogFlowRequests dlg = new DialogFlowRequests();
                    Flow_Sequence data = dlg.setData(sequncesJlist.getSelectedValue().getFlow_sequence());
//                    if(data!=null){
//                        sequncesJlist.getSelectedValue().getFlow_sequence().(data);
//                    }
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
