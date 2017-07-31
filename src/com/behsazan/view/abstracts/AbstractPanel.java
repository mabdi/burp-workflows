package com.behsazan.view.abstracts;

import javax.swing.*;

/**
 * Created by admin on 07/29/2017.
 */
public abstract class AbstractPanel extends JPanel {

    public AbstractPanel() {
        initModel();
        initUI();
    }

    protected void initModel() {    }

    protected abstract void initUI();

    public void shutDown() {

    }
}
