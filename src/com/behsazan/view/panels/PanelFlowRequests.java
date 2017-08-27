package com.behsazan.view.panels;

import burp.BurpExtender;
import burp.IBurpExtenderCallbacks;
import burp.ITextEditor;
import com.behsazan.model.adapters.TableModelResponseOut;
import com.behsazan.model.entity.ResponseOut;
import com.behsazan.model.entity.Flow_Request;
import com.behsazan.model.entity.Flow_Sequence;
import com.behsazan.model.settings.Settings;
import com.behsazan.view.abstracts.AbstractPanel;
import com.behsazan.view.dialogs.DialogResponseOutput;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * Created by admin on 08/21/2017.
 */
public class PanelFlowRequests extends AbstractPanel {
    private JSplitPane splitPane;
    private JList<Flow_Request> listRequests;
    private JSplitPane verticalSplitPane;
    private DefaultListModel<Flow_Request> modelRequests;
    private JTabbedPane tabs;
    private ITextEditor requestViewer;
    private ITextEditor responseViewer;
//    private JTabbedPane parametersTabs;
//    private JPanel requestInputPanel;
    private JPanel responseOutputPanel;
    private JTable jtableResponseOut;
//    private JTable jtableRequestIn;
//    private TableModelRequestIn modelRequestIn;
    private TableModelResponseOut modelResponseOut;
    private Flow_Request currentRequest;

    @Override
    protected void initUI() {
        setLayout(new BorderLayout());
        add(getSplitPane(), BorderLayout.CENTER);
    }

    public JSplitPane getSplitPane() {
        if (splitPane == null) {
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setLeftComponent(new JScrollPane(getRequestList()));
            splitPane.setRightComponent(getRequestTabs());
            splitPane.setDividerLocation(0.3);
        }
        return splitPane;
    }

    public JList getRequestList() {
        if (listRequests == null) {
            modelRequests = new DefaultListModel<>();
            listRequests = new JList<>(modelRequests);
            listRequests.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listRequests.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        updateRequestDetail();
                        currentRequest = listRequests.getSelectedValue();
                        showRequestDetail();
                    }
                }
            });
        }
        return listRequests;
    }


    public JSplitPane getVerticalSplitPane() {
        if (verticalSplitPane == null) {
            verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            IBurpExtenderCallbacks callbacks = BurpExtender.getInstance().getCallbacks();
            responseViewer = callbacks.createTextEditor();
            verticalSplitPane.setTopComponent(responseViewer.getComponent());
            verticalSplitPane.setBottomComponent(getResponseOutputPanel());
            verticalSplitPane.setResizeWeight(0.5);
        }
        return verticalSplitPane;
    }


    public void updateRequestDetail() {
        if (currentRequest == null) return;
        currentRequest.setModifiedRequest(requestViewer.getText());
    }

    private void showRequestDetail() {
        if (currentRequest.getModifiedRequest() == null) {
            currentRequest.setModifiedRequest(currentRequest.getRequest().getRequest());
        }
        requestViewer.setText(currentRequest.getModifiedRequest());
        responseViewer.setText(currentRequest.getRequest().getResponse());
//        modelRequestIn.changeData(currentRequest.getInputParams());
        modelResponseOut.changeData(currentRequest.getOutputParams());
        getRequestTabs().setSelectedIndex(0);
    }

    public JTabbedPane getRequestTabs() {
        if (tabs == null) {
            tabs = new JTabbedPane();
            IBurpExtenderCallbacks callbacks = BurpExtender.getInstance().getCallbacks();
//            requestViewer = callbacks.createMessageEditor(null, true);
//            responseViewer = callbacks.createMessageEditor(null, false);
            requestViewer = callbacks.createTextEditor();
            JPanel reqPanel = new JPanel(new BorderLayout());
            JPanel reqToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton btnReqParam = new JButton("Add param var");
            btnReqParam.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addPlaceHolder(requestViewer, Settings.PARAM_IDENTIFIER);
                }
            });
            JButton btnReqLocal = new JButton("Add local var");
            btnReqLocal.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addPlaceHolder(requestViewer, Settings.LOCAL_IDENTIFIER);
                }
            });
            JButton btnReqGlobal = new JButton("Add Global var");
            btnReqGlobal.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addPlaceHolder(requestViewer, Settings.GLOBAL_IDENTIFIER);
                }
            });
            reqToolbar.add(btnReqParam);
            reqToolbar.add(btnReqLocal);
            reqToolbar.add(btnReqGlobal);

            reqPanel.add(requestViewer.getComponent(), BorderLayout.CENTER);
            reqPanel.add(reqToolbar, BorderLayout.NORTH);

            tabs.addTab("Request", reqPanel);
            tabs.addTab("Response", getVerticalSplitPane());
//            tabs.addChangeListener(new ChangeListener() {
//                @Override
//                public void stateChanged(ChangeEvent e) {
//                    getParametersTabs().setSelectedIndex(tabs.getSelectedIndex());
//                }
//            });
        }
        return tabs;
    }

    private void addPlaceHolder(ITextEditor editor, String placeHoder) {
        if (editor.getSelectedText() != null && editor.getSelectedText().length > 0) {
            int[] bnd = editor.getSelectionBounds();
            byte[] text = editor.getText();
            byte[] newText = new byte[bnd[0] + placeHoder.length() + (text.length - bnd[1])];
            System.arraycopy(text, 0, newText, 0, bnd[0]);
            System.arraycopy(placeHoder.getBytes(), 0, newText, bnd[0], placeHoder.getBytes().length);
            System.arraycopy(text, bnd[1], newText, bnd[0] + placeHoder.getBytes().length, text.length - bnd[1] );
            editor.setText(newText);
            editor.setSearchExpression(Settings.SECTION_CHAR);
        }
    }

//    public JTabbedPane getParametersTabs() {
//        if (parametersTabs == null) {
//            parametersTabs = new JTabbedPane();
//
////            parametersTabs.addTab("Request Input", getRequestInputPanel());
//            parametersTabs.addTab("Response Output", getResponseOutputPanel());
//        }
//        return parametersTabs;
//    }

//    public JPanel getRequestInputPanel() {
//        if (requestInputPanel == null) {
//            requestInputPanel = new JPanel(new BorderLayout());
//            JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
//            JButton addSequence = new JButton("+");
//            addSequence.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    if (listRequests.getSelectedIndex() < 0) {
//                        return;
//                    }
//                    DialogRequestInput dlg = new DialogRequestInput(PanelParamSequenceRequests.this);
//                    RequestIn requestIn = dlg.getData(listRequests.getSelectedValue());
//                    if (requestIn != null) {
//                        listRequests.getSelectedValue().addInputParam(requestIn);
//                        modelRequestIn.fireTableDataChanged();
//                    }
//                }
//            });
//            JButton edit = new JButton("?");
//            edit.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    if (getJtableRequestIn().getSelectedRow() < 0) {
//                        return;
//                    }
//                    DialogRequestInput dlg = new DialogRequestInput(PanelParamSequenceRequests.this);
//                    dlg.getEditData(modelRequestIn.getItem(getJtableRequestIn().getSelectedRow()));
//                }
//            });
//            JButton removeSequence = new JButton("-");
//            removeSequence.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    if (getJtableRequestIn().getSelectedRow() < 0) {
//                        return;
//                    }
//                    listRequests.getSelectedValue().getInputParams().remove(getJtableRequestIn().getSelectedRow());
//                    modelRequestIn.fireTableDataChanged();
//                }
//            });
//            btns.add(addSequence);
//            btns.add(edit);
//            btns.add(removeSequence);
//            requestInputPanel.add(btns, BorderLayout.NORTH);
//            requestInputPanel.add(new JScrollPane(getJtableRequestIn()), BorderLayout.CENTER);
//        }
//        return requestInputPanel;
//    }

    public JPanel getResponseOutputPanel() {
        if (responseOutputPanel == null) {
            responseOutputPanel = new JPanel(new BorderLayout());
            JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton addSequence = new JButton("+");
            addSequence.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (listRequests.getSelectedIndex() < 0) {
                        return;
                    }

                    JPopupMenu popup = new JPopupMenu();
                    Map<Integer, String> kvp = ResponseOut.getTypesString();
                    for (String v:kvp.values() ) {
                        JMenuItem jmi = new JMenuItem(v);
                        final String data = v;
                        jmi.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                DialogResponseOutput dlg = new DialogResponseOutput();
                                ResponseOut responseOut = dlg.getData( data );
                                if (responseOut != null) {
                                    listRequests.getSelectedValue().addOutputParam(responseOut);
                                    modelResponseOut.fireTableDataChanged();
                                }
                            }
                        });
                        popup.add(jmi);
                    }
                    // show on the button?
                    popup.show((Component)e.getSource(), 0, ((Component) e.getSource()).getHeight());
                }
            });
            JButton edit = new JButton("?");
            edit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (getJtableResponseOut().getSelectedRow() < 0) {
                        return;
                    }
                    DialogResponseOutput dlg = new DialogResponseOutput();
                    dlg.getEditData(modelResponseOut.getItem(getJtableResponseOut().getSelectedRow()));
                }
            });
            JButton removeSequence = new JButton("-");
            removeSequence.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (getJtableResponseOut().getSelectedRow() < 0) {
                        return;
                    }
                    listRequests.getSelectedValue().getOutputParams().remove(getJtableResponseOut().getSelectedRow());
                    modelResponseOut.fireTableDataChanged();
                }
            });
            btns.add(addSequence);
            btns.add(edit);
            btns.add(removeSequence);
            responseOutputPanel.add(btns, BorderLayout.NORTH);
            responseOutputPanel.add(new JScrollPane(getJtableResponseOut()), BorderLayout.CENTER);
        }
        return responseOutputPanel;
    }

    public JTable getJtableResponseOut() {
        if (jtableResponseOut == null) {
            modelResponseOut = new TableModelResponseOut();
            jtableResponseOut = new JTable();
            jtableResponseOut.setModel(modelResponseOut);
        }
        return jtableResponseOut;
    }

//    public JTable getJtableRequestIn() {
//        if (jtableRequestIn == null) {
//            modelRequestIn = new TableModelRequestIn();
//            jtableRequestIn = new JTable();
//            jtableRequestIn.setModel(modelRequestIn);
//        }
//        return jtableRequestIn;
//    }

    public void setData(Flow_Sequence sequence) {
        for (Flow_Request obj : sequence.getRequests()) {
            modelRequests.addElement(obj);
        }
    }
}
