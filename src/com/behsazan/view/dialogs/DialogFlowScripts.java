package com.behsazan.view.dialogs;

import com.behsazan.model.adapters.TableModelScriptsSelect;
import com.behsazan.model.entity.Script;
import com.behsazan.view.abstracts.AbstractDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Created by abdi_m on 11/21/2017.
 */
public class DialogFlowScripts extends AbstractDialog {
    private JPanel topPanel;
    private JPanel selectPanel;
    private JPanel buttonsPanel;
    private JTable tableAll;
    private TableModelScriptsSelect tableAllModel;
    private JScrollPane tableAllScroll;
    private JScrollPane tableSelecetedScroll;
    private JTable tableSelected;
    private TableModelScriptsSelect tableSelectedModel;
    private JPopupMenu popup;
    private List<Script> result;

    public DialogFlowScripts() {
        super(false);
    }

    @Override
    protected void initUI() {
        setSize(800, 600);
        setTitle("Flow Scripts");
        setLocationRelativeTo(getParentWindow());
        installEscapeCloseOperation();
        setLayout(new BorderLayout());
        add(getTopPanel(), BorderLayout.NORTH);
        add(getCenterPanel(), BorderLayout.CENTER);
        add(getButtonsPanel(), BorderLayout.SOUTH);

    }

    public List<Script> getData() {
        setVisible(true);
        return result;
    }

    public List<Script> getData(List<Script> scripts) {
        for (Script s1 : scripts) {
            for (Script s2 : tableAllModel.getData()) {
                if (s2.getId() == s1.getId()) {
                    tableSelectedModel.getData().add(s1);
                }
            }
        }
        tableSelectedModel.fireTableDataChanged();
        return getData();
    }

    private JPanel getCenterPanel() {
        if(selectPanel == null) {
            selectPanel = new JPanel();
            selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.LINE_AXIS));
            selectPanel.add(Box.createRigidArea(new Dimension(10, 0)));
            selectPanel.add(getTableAllScripts());
            selectPanel.add(Box.createRigidArea(new Dimension(5, 0)));
            selectPanel.add(getSelectButtons());
            selectPanel.add(Box.createRigidArea(new Dimension(5, 0)));
            selectPanel.add(getTableSelectedScripts());
            selectPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        }
        return selectPanel;
    }

    public JPanel getTopPanel() {
        if(topPanel==null){
            topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.add(new JLabel("Select Scrips that you want to inject in the test:"));
        }
        return topPanel;
    }

    public JPanel getButtonsPanel() {
        if(buttonsPanel == null){
            buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dissmiss();
                }
            });
            JButton okBtn = new JButton("Select");
            okBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    result = tableSelectedModel.getData();
                    dissmiss();
                }
            });

            JButton newBtn = new JButton("New Script");
            newBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DialogScriptNew dlg = new DialogScriptNew();
                    dlg.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            tableAllModel.loadData();
                            tableAllModel.fireTableDataChanged();
                        }
                    });
                }
            });
            buttonsPanel.add(newBtn);
            buttonsPanel.add(cancelBtn);
            buttonsPanel.add(okBtn);
        }
        return buttonsPanel;
    }

    public JScrollPane getTableAllScripts() {
        if(tableAllScroll == null){
            tableAll = new JTable();
            tableAllModel = new TableModelScriptsSelect();
            tableAllModel.loadData();
            tableAll.setModel(tableAllModel);
            tableAll.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tableAllScroll = new JScrollPane(tableAll);
            tableAll.addMouseListener(new MouseAdapter()
            {
                public void mousePressed(MouseEvent e)
                {
                }

                public void mouseReleased(MouseEvent e)
                {
                    if (e.isPopupTrigger())
                    {
                        JTable source = (JTable)e.getSource();
                        int row = source.rowAtPoint( e.getPoint() );
                        int column = source.columnAtPoint( e.getPoint() );

                        if (! source.isRowSelected(row))
                            source.changeSelection(row, column, false, false);

                        getJtablePopup(tableAll).show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
        }
        return tableAllScroll;
    }

    public JScrollPane getTableSelectedScripts() {
        if(tableSelecetedScroll == null){
            tableSelected = new JTable();
            tableSelectedModel = new TableModelScriptsSelect();
            tableSelected.setModel(tableSelectedModel);
            tableSelected.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tableSelecetedScroll = new JScrollPane(tableSelected);
            tableSelected.addMouseListener(new MouseAdapter()
            {
                public void mousePressed(MouseEvent e)
                {
                }

                public void mouseReleased(MouseEvent e)
                {
                    if (e.isPopupTrigger())
                    {
                        JTable source = (JTable)e.getSource();
                        int row = source.rowAtPoint( e.getPoint() );
                        int column = source.columnAtPoint( e.getPoint() );

                        if (! source.isRowSelected(row))
                            source.changeSelection(row, column, false, false);

                        getJtablePopup(tableSelected).show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
        }
        return tableSelecetedScroll;
    }

    private JPopupMenu getJtablePopup(final JTable table) {
        if(popup == null) {
            popup = new JPopupMenu();
            JMenuItem menuItemEdit = new JMenuItem("Edit");
            menuItemEdit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int tableSelectedRow = table.getSelectedRow();
                    int id = (Integer) table.getModel().getValueAt(tableSelectedRow,0);
                    DialogScriptEdit dlg = new DialogScriptEdit(id);
                }
            });
            popup.add(menuItemEdit);
        }
        return  popup;
    }

    public Component getSelectButtons() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton buttonin = new JButton(">>");
        buttonin.setToolTipText("Select");
        buttonin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableSelectedModel.getData().add(tableAllModel.getData().get(tableAll.getSelectedRow()));
                tableSelectedModel.fireTableDataChanged();
            }
        });
        buttonPanel.add(buttonin);

        JButton buttonout = new JButton("<<");
        buttonout.setToolTipText("Deselect");
        buttonout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tableSelected.getSelectedRow() > 0) {
                    tableSelectedModel.getData().remove(tableSelected.getSelectedRow());
                    tableSelectedModel.fireTableDataChanged();
                }
            }
        });
        buttonPanel.add(buttonout);

        return buttonPanel;
    }
}
