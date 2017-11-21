package com.behsazan.view;

import burp.BurpExtender;
import burp.ITab;
import com.behsazan.model.settings.Settings;
import com.behsazan.view.abstracts.AbstractTab;
import com.behsazan.view.tabs.*;

import javax.swing.*;
import java.awt.*;

/**
 * Created by admin on 07/29/2017.
 */
public class Interface implements ITab {

    private final BurpExtender context;
    private JTabbedPane tabsPane;

    public Interface(){
        this.context = BurpExtender.getInstance();
        initUI();
    }

    private void initUI() {
        tabsPane = new JTabbedPane();
        AbstractTab[] tabs = new AbstractTab[]{
                (Settings.isShowTabSequence())? new TabSequnces():null,
                (Settings.isShowTabScript())? new TabScripts():null,
                new TabFlow(),
                new TabLogins(),
                new TabScenarios(),
                new TabSettings(),
        };
        for (AbstractTab tab : tabs) {
            if(tab!=null) {
                tabsPane.addTab(tab.getTabTitle(), tab);
            }
        }
    }

    @Override
    public String getTabCaption() {
        return BurpExtender.getExtensionName();
    }

    @Override
    public Component getUiComponent() {
        return tabsPane;
    }
}
