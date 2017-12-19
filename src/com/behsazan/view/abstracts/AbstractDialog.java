package com.behsazan.view.abstracts;

import burp.BurpExtender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

/**
 * Created by admin on 07/29/2017.
 */
public abstract class AbstractDialog extends JDialog {
    private static final KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    public static final String dispatchWindowClosingActionMapKey = "com.spodding.tackline.dispatch:WINDOW_CLOSING";

    private final Component parent_window;
//    private boolean unClosable = false;


    public AbstractDialog(){
        this(true);
    }

    public AbstractDialog(final boolean setVisible) {
        this.parent_window = BurpExtender.getUiParent();
        setModalityType(ModalityType.APPLICATION_MODAL);
        initUI();
        if(setVisible) {
            setVisible(true);
        }
    }

    protected abstract void initUI();

    protected Component getParentWindow(){return parent_window;}

    public void dissmiss(){
        dispose();
    }


    public void installEscapeCloseOperation() {

        Action dispatchClosing = new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                    AbstractDialog.this.dispatchEvent(new WindowEvent(AbstractDialog.this, WindowEvent.WINDOW_CLOSING));
            }
        };
        JRootPane root = this.getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put( escapeStroke, dispatchWindowClosingActionMapKey );
        root.getActionMap().put( dispatchWindowClosingActionMapKey, dispatchClosing );
    }



//    @Override
//    protected JRootPane createRootPane() {
//        JRootPane rootPane = new JRootPane();
//        KeyStroke stroke_esc = KeyStroke.getKeyStroke("ESCAPE");
//        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
//        inputMap.put(stroke_esc, "ESCAPE");
//        rootPane.getActionMap().put("ESCAPE", new AbstractAction() {
//            public void actionPerformed(ActionEvent actionEvent) {
//                if(!unClosable){
//                    dissmiss();
//                }
//            }
//        });
//
//        return rootPane;
//    }

//    public void setUnClosable(){
//        this.unClosable = true;
//    }

    public void setAsDefaultButton(JButton okButton){
        getRootPane().setDefaultButton(okButton);
    }

}
