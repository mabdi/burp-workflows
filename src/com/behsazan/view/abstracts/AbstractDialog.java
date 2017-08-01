package com.behsazan.view.abstracts;

import javax.swing.*;

/**
 * Created by admin on 07/29/2017.
 */
public abstract class AbstractDialog extends JDialog {

    private final JPanel parent_window;


    public AbstractDialog(JPanel parent){
        this(parent,true);
    }

    public AbstractDialog(JPanel parent,boolean setVisible){
        this.parent_window = parent;
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

    protected JPanel getParentWindow(){return parent_window;}

    public void dissmiss(){
        dispose();
    }

}
