package com.behsazan.model.settings;

import burp.BurpExtender;
import com.behsazan.model.sqlite.SqliteHelper;
import com.behsazan.view.tabs.TabSettings;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Created by admin on 07/30/2017.
 */
public class Settings {

    public static final long DELAY = 300;
    public static String TADAROKAT_protocol = "http";
    public static String TADAROKAT_HOST = "172.16.27.12";
    public static int TADAROKAT_PORT = 9080;
    public static String TADAROKAT_PATH = "/eproc4qa";

    public static void backupDb(TabSettings tabSettings) {
        SqliteHelper db = new SqliteHelper();
        db.closeAllConnection();
        File f = db.getDbFile();
        File f2 = new File(f.getParent(),"db_"+System.currentTimeMillis()+".db");
        try {
//            FileUtils.copyFile(f,f2);
            Files.copy( f.toPath(),
                    f2.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            Desktop.getDesktop().open(f.getParentFile());
        } catch (IOException e1) {
            e1.printStackTrace(BurpExtender.getInstance().getStdout());
            JOptionPane.showMessageDialog(tabSettings,"Backup Failed.","Error",JOptionPane.ERROR_MESSAGE);
        }
    }
}
