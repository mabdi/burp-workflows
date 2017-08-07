package com.behsazan.view.tabs;

import burp.BurpExtender;
import com.behsazan.model.settings.Settings;
import com.behsazan.model.sqlite.SqliteHelper;
import com.behsazan.view.UIUtils;
import com.behsazan.view.abstracts.AbstractTab;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;

/**
 * Created by admin on 07/29/2017.
 */
public class TabSettings extends AbstractTab {


    private JButton backupButton;

    @Override
    protected void initUI() {
        add(getBackupButton());
    }

    @Override
    public String getTabTitle() {
        return "Settings";
    }

    public JButton getBackupButton() {
        if(backupButton == null){
            backupButton = new JButton("Backup");
            backupButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    UIUtils.invokeNotInDispatchThreadIfNeeded(new Runnable() {
                        @Override
                        public void run() {
                            Settings.backupDb(TabSettings.this);
                        }
                    });

                }
            });
        }
        return backupButton;
    }
}
