package com.behsazan.view.abstracts;

import javax.swing.*;

/**
 * Created by admin on 07/29/2017.
 */
public abstract class AbstractTab extends JPanel {

    abstract protected void initUI();

    abstract public String getTabTitle();

    public AbstractTab(){
        initUI();
    }

}
