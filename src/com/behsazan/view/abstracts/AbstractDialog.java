package com.behsazan.view.abstracts;

import javax.swing.*;
import java.awt.*;

/**
 * Created by admin on 07/29/2017.
 */
public abstract class AbstractDialog extends JDialog {

    private final Component parent_window;


    public AbstractDialog(Component parent){
        this(parent,true);
    }

    public AbstractDialog(Component parent,boolean setVisible){
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

    protected Component getParentWindow(){return parent_window;}

    public void dissmiss(){
        dispose();
    }

}
