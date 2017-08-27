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
    public static final String SESSION_COOKIENAME = "JSESSIONID";
    public static final String SECTION_CHAR = "\u00A7";
    public static final String LOCAL_IDENTIFIER = "\u00A7" + "var@locals" + "\u00A7";
    public static final String LOCAL_PATTERN = "\u00A7" + "(\\w+)@locals" + "\u00A7";
    public static final String GLOBAL_IDENTIFIER = "\u00A7" + "var@globals" + "\u00A7";
    public static final String GLOBAL_PATTERN = "\u00A7" + "(\\w+)@globals" + "\u00A7";
    public static final String PARAM_IDENTIFIER = "\u00A7" + "var@params" + "\u00A7";
    public static final String PARAM_PATTERN = "\u00A7" + "(\\w+)@params" + "\u00A7";
    public static final String[] BASE_URLS = new String[]{
            "http://172.16.27.12:9080/eproc4qa/",
            "http://172.16.27.12:9080/eproc4dev/"
    };


    public static void backupDb(TabSettings tabSettings) {
        SqliteHelper db = new SqliteHelper();
        db.closeAllConnection();
        File f = db.getDbFile();
        File f2 = new File(f.getParent(),"db_"+System.currentTimeMillis()+".db");
        try {
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
