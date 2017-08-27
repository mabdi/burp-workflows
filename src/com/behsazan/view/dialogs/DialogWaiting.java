package com.behsazan.view.dialogs;

import javax.swing.*;
import java.awt.*;

/**
 * Created by admin on 08/21/2017.
 */
public class DialogWaiting extends JDialog {

    private final JLabel dialogWaitlabel;
    private final Component parent;

    public DialogWaiting(Component parent) {
        JPanel panel = new JPanel();
        this.parent = parent;
        dialogWaitlabel = new JLabel("Please wait...");
        panel.add(dialogWaitlabel );
        add(panel);
        setTitle("Please wait...");
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        pack();
        setLocationRelativeTo(parent);
    }

    public void setMessage(String str){
        dialogWaitlabel.setText(str);
    }

    public static DialogWaiting showWaitingDialog(Component parent) {
        DialogWaiting pleaseWaitDialog = new DialogWaiting(parent);
        return pleaseWaitDialog;
    }

    public static void updateMessage(DialogWaiting pleaseWaitDialog,String msg){
        pleaseWaitDialog.setMessage(msg);
        pleaseWaitDialog.pack();
        pleaseWaitDialog.setLocationRelativeTo(pleaseWaitDialog.parent);
        pleaseWaitDialog.repaint();
    }

    public static  void closeWaitingDialog(DialogWaiting pleaseWaitDialog){
        if(pleaseWaitDialog != null) {
            pleaseWaitDialog.dispose();
        }
    }
}
