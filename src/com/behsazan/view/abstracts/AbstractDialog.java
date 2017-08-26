package com.behsazan.view.abstracts;

import burp.BurpExtender;

import javax.swing.*;
import java.awt.*;

/**
 * Created by admin on 07/29/2017.
 */
public abstract class AbstractDialog extends JDialog {

    private final Component parent_window;


    public AbstractDialog(){
        this(true);
    }

    public AbstractDialog(boolean setVisible){
        this.parent_window = BurpExtender.getUiParent();
        setModalityType(ModalityType.APPLICATION_MODAL);
        preInitUI();
        initUI();
        postInitUI();
        if(setVisible) {
            setVisible(true);
        }
    }

    protected void postInitUI() {

    }

    protected void preInitUI() {

    }

    protected abstract void initUI();

    protected Component getParentWindow(){return parent_window;}

    public void dissmiss(){
        dispose();
    }

}
