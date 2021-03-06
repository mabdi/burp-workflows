package com.behsazan.view.dialogs;

import burp.BurpExtender;
import com.behsazan.model.adapters.SequenceListModelObject;
import com.behsazan.model.entity.Flow;
import com.behsazan.model.entity.Flow_Sequence;
import com.behsazan.model.entity.Request;
import com.behsazan.model.entity.Script;
import com.behsazan.model.settings.Settings;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by admin on 08/02/2017.
 */
public class DialogFlowEdit extends AbstractDialog {

    private JPanel topPanel;
    private JSplitPane centerSplitPanel;
    private JPanel buttonsPanel;
    private JTextField txtFlowName;
    private JPanel sequenceJListPanel;
    private JPanel sequenceDetailPanel;
    private JList<SequenceListModelObject> sequncesJlist;
//    private JTextField txtRootAddress;
//    private JTextField txtBase1;
//    private JTextField txtBase2;
    private JPanel centerPanel;
    private DefaultListModel<SequenceListModelObject> modelSequeces;
    private JButton btnRequest;
    private SequenceListModelObject activeSequence;
    private Flow flow;
//    private JComboBox<String> cmbCookie;
//    private DefaultComboBoxModel<String> modelCookie;
//    private Vector<Vector<Object>> vectorCookie;
    private JTextArea txtParam;
    private JTextField txtDescription;
    private List<Script> selectedScripts;
    private JTextField txtCookie;
    private JButton btnCookie;

    public DialogFlowEdit() {
        super(false);
    }

    public void initData(final int flowId) {
        final DialogWaiting pleaseWaitDialog = DialogWaiting.showWaitingDialog(this);
        final SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {

            @Override
            protected Void doInBackground() throws Exception {
                flow = Flow.getById(flowId);
                for (Flow_Sequence seq: flow.getSeqs()) {
                    SequenceListModelObject s = new SequenceListModelObject(seq.getSequence());
                    s.setFlow_sequence(seq);
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
        txtFlowName.setText(flow.getName());
        txtDescription.setText(flow.getDescription());
        txtParam.setText(flow.getParameters());
        setVisible(true);
    }

    @Override
    protected void initUI() {
        setSize(800,600);
        setTitle("Edit Flow");
        setLocationRelativeTo(getParentWindow());
        installEscapeCloseOperation();
        setLayout(new BorderLayout());
        add(getTopPanel(), BorderLayout.NORTH);
        add(getCenterSplitPanel(), BorderLayout.CENTER);
        add(getButtonsPanel(), BorderLayout.SOUTH);
    }

    public JPanel getTopPanel() {
        if (topPanel == null) {
            topPanel = new JPanel(new GridBagLayout());
            UIUtils.FormUtility form = new UIUtils.FormUtility(topPanel);
            form.addLabel("Name :");
            form.addLastField(getTxtFlowName());

            form.addLabel("Description :");
            form.addLastField(getTxtDescription());

            form.addLabel("Parameters (comma separated):");
            txtParam = new JTextArea(2, 10);
            txtParam.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());
            form.addLastField(new JScrollPane(txtParam));


            form.addLabel("Scripts:");
            JPanel jpn = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton btn = new JButton("Manage Scripts");
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DialogFlowScripts dlg = new DialogFlowScripts();
                    List<Script> scripts = dlg.getData(flow.getScripts());
                    if(scripts!=null){
                        selectedScripts = scripts;
                    }
                }
            });
            jpn.add(btn);
            form.addLastField(jpn);

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
                    String name = txtFlowName.getText();
                    String description = txtDescription.getText();
                    String params = txtParam.getText();
                    if(name.isEmpty() ){
                        JOptionPane.showMessageDialog(DialogFlowEdit.this,"Some required filed is not set.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if(reqs.size()==0){
                        JOptionPane.showMessageDialog(DialogFlowEdit.this,"No sequence is added.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try {
                        List<Flow_Sequence> ts = new ArrayList<>();
                        for (SequenceListModelObject req: reqs) {
                            ts.add(req.getFlow_sequence());
                        }
                        flow.setSeqs(ts);
                        flow.setName(name);
                        flow.setDescription(description);
                        flow.setParameters(params);
                        if(selectedScripts == null){
                            flow.setScripts(new ArrayList<Script>());
                        }else {
                            flow.setScripts(selectedScripts);
                        }
                        Flow.updateFlow(flow);
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

    public JTextField getTxtFlowName() {
        if(txtFlowName == null){
            txtFlowName = new JTextField("",20);
            txtFlowName.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());
            txtFlowName.requestFocus();
        }
        return txtFlowName;
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
                    int rsp = JOptionPane.showConfirmDialog(DialogFlowEdit.this,"Are you sure to delete?","Delete",JOptionPane.YES_NO_OPTION);
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
//        try {
//            new URL(txtRootAddress.getText());
//
//        } catch (MalformedURLException e) {
//            JOptionPane.showMessageDialog(this,"URL format is not correct.","Error",JOptionPane.ERROR_MESSAGE);
//            return false;
//        }
//        if(txtBase1.getText().isEmpty() || txtBase2.getText().isEmpty() || txtRootAddress.getText().isEmpty()){
//            JOptionPane.showMessageDialog(this,"Some required filed is not set.","Error",JOptionPane.ERROR_MESSAGE);
//            return false;
//        }
        return  true;

    }

    private void updateSequenceDetail() {
        if(activeSequence==null)
            return;
//        try {
//            activeSequence.getFlow_sequence().setUrl(new URL(txtRootAddress.getText()));
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        activeSequence.getFlow_sequence().setBase1(txtBase1.getText());
//        activeSequence.getFlow_sequence().setBase2(txtBase2.getText());

        activeSequence.getFlow_sequence().setCookie(txtCookie.getText().trim());


//        if(cmbCookie.getSelectedIndex()>0) {
//            String param = (String) vectorCookie.get(cmbCookie.getSelectedIndex() - 1).get(3);
//            activeSequence.getFlow_sequence().setCookie(param);
//        }else{
//            activeSequence.getFlow_sequence().setCookie("");
//        }
    }

    private void showSequenceDetail() {
        Request req1 = activeSequence.getSequence().getRequest().get(0);
        txtCookie.setEnabled(true);
        btnCookie.setEnabled(true);

//        cmbCookie.setEnabled(true);
        String cookeOutParam = activeSequence.getFlow_sequence().getCookie();
        txtCookie.setText(cookeOutParam);

//        cmbCookie.setEnabled(true);
//        String cookeOutParam = activeSequence.getFlow_sequence().getCookie();
//        cmbCookie.setSelectedIndex(0);
//        if(!cookeOutParam.isEmpty()){
//            int i=1;
//            for(Vector<Object> obj: vectorCookie){
//                if(obj.get(3).equals(cookeOutParam)){
//                    cmbCookie.setSelectedIndex(i);
//                    break;
//                }
//                i++;
//            }
//        }
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
                if(sequenceListModelObject.getFlow_sequence()!=null){
                    return;
                }
                publish("Parsing " + sequenceListModelObject.getSequence().getName());
                sequenceListModelObject.setFlow_sequence(Flow_Sequence.initBySequence(sequenceListModelObject.getSequence()));
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
            sequenceDetailPanel = new JPanel(new GridBagLayout());
            UIUtils.FormUtility form = new UIUtils.FormUtility(sequenceDetailPanel);
            form.addLabel("Change Cookie: ");
            txtCookie = new JTextField();
            txtCookie.setEnabled(false);
            form.addMiddleField(txtCookie);
            btnCookie = new JButton("From variables");
            btnCookie.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem jmi = new JMenuItem(Settings.LOCAL_IDENTIFIER.replace("var","cookie"));
                    jmi.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtCookie.setText(Settings.LOCAL_IDENTIFIER.replace("var","cookie"));
                        }
                    });
                    popup.add(jmi);
                    JMenuItem jmi2 = new JMenuItem(Settings.GLOBAL_IDENTIFIER.replace("var","cookie"));
                    jmi2.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtCookie.setText(Settings.GLOBAL_IDENTIFIER.replace("var","cookie"));
                        }
                    });
                    popup.add(jmi2);
                    JMenuItem jmi3 = new JMenuItem(Settings.PARAM_IDENTIFIER.replace("var","cookie"));
                    jmi3.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtCookie.setText(Settings.PARAM_IDENTIFIER.replace("var","cookie"));
                        }
                    });
                    popup.add(jmi3);

                    popup.show((Component)e.getSource(), 0, ((Component) e.getSource()).getHeight());
                }
            });
            btnCookie.setEnabled(false);
            JPanel jpn = new JPanel(new FlowLayout(FlowLayout.LEFT  ));
            jpn.add(btnCookie);
            form.addLastField(jpn);

//            modelCookie = new DefaultComboBoxModel<String>();
//            vectorCookie = Login.getAllLogins_Table();
//            modelCookie.addElement("none");
//            for(Vector<Object> login: vectorCookie){
//                modelCookie.addElement(""+ login.get(0) + ". " + login.get(1) + " (" + login.get(3) + ")");
//            }
//            cmbCookie = new JComboBox<String>(modelCookie);
//            cmbCookie.setEnabled(false);
//            form.addLastField(cmbCookie);

            form.addLabel("Edit Requests: ");
            btnRequest = new JButton("Edit Requests");
            btnRequest.setEnabled(false);
            btnRequest.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DialogFlowRequests dlg = new DialogFlowRequests();
                    Flow_Sequence data = dlg.setData(sequncesJlist.getSelectedValue().getFlow_sequence(), txtParam.getText());
                    if(data!=null){
                        sequncesJlist.getSelectedValue().setFlow_sequence(data);
                    }
                }
            });
            JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jp.add(btnRequest);
            form.addLastField(jp);
            form.fillReminder();
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

    public JTextField getTxtDescription() {
        if(txtDescription == null){
            txtDescription = new JTextField("",20);
            txtDescription.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());
        }
        return txtDescription;
    }
}
