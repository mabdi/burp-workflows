package com.behsazan.model.settings;

import burp.BurpExtender;
import com.behsazan.model.sqlite.SettingDb;
import com.behsazan.model.sqlite.SqliteHelper;
import com.behsazan.view.tabs.TabSettings;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

/**
 * Created by admin on 07/30/2017.
 */
public class Settings {
    public final static String TABLE_KEY_DELAY = "DELAY";
    public final static String TABLE_KEY_SESSION = "SESSION_COOKIE";
    public final static String TABLE_KEY_FILTER = "FILTER";
    public final static String TABLE_KEY_URLS = "URLS";
    public final static String TABLE_SHOW_TAB_SEQUENCE = "SHOW_TAB_SEQUENCE";
    public final static String TABLE_SHOW_TAB_SCRIPT = "SHOW_TAB_SCRIPT";

    private static final long DELAY = 300;
    private static final String SESSION_COOKIENAME = "JSESSIONID";
    private static final String SHOW_TAB_SEQUENCE = "1";
    private static final String SHOW_TAB_SCRIPT = "1";
    public static final String SECTION_CHAR = "\u00A7";
    public static final String LOCAL_IDENTIFIER = "\u00A7" + "var@locals" + "\u00A7";
    public static final String LOCAL_PATTERN = "\u00A7" + "(\\w+)@locals" + "\u00A7";
    public static final String GLOBAL_IDENTIFIER = "\u00A7" + "var@globals" + "\u00A7";
    public static final String GLOBAL_PATTERN = "\u00A7" + "(\\w+)@globals" + "\u00A7";
    public static final String PARAM_IDENTIFIER = "\u00A7" + "var@params" + "\u00A7";
    public static final String PARAM_PATTERN = "\u00A7" + "(\\w+)@params" + "\u00A7";
    private static final String[] BASE_URLS = new String[]{
            "http://localhost:8000/app4qa/",
            "http://localhost:8001/app4dev/"
    };

    private static final String[] RECORD_FILTER = new String[]{
            ".css",
            ".js",
            ".png",
            ".gif",
            ".jpg",
            ".pdf"
    };
    public static final String FILENAME_ON_TEST_START = "on_test_start";
    public static final String FILENAME_ON_TEST_FINISH = "on_test_finish";
    public static final String FILENAME_ON_SEQUENCE_START = "on_sequence_start";
    public static final String FILENAME_ON_SEQUENCE_FINISH = "on_sequence_finish";
    public static final String FILENAME_ON_REQUEST_BEFORE_ASSIGNMENT = "on_request_before";
    public static final String FILENAME_ON_REQUEST_AFTER_ASSIGNMENT = "on_request_after";
    public static final String FILENAME_ON_RESPONSE_RECEIVED = "on_response";


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

    public static  long getDelay(){
        String val = loadConfigFromDb(TABLE_KEY_DELAY, String.valueOf(DELAY) );
        return Long.parseLong(val);
    }

    private static  String loadConfigFromDb(String key,String default_val){
        String retval = default_val;
        try {
            retval = new SettingDb().loadSettings(key);
            if(retval == null){
                retval = default_val;
                new SettingDb().updateKey(key, default_val);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return retval;
    }

    private static  String[] loadConfigFromDb(String key,String[] default_val){
        String[] retval = default_val;
        try {
            String val = new SettingDb().loadSettings(key);
            if(val == null){
                new SettingDb().updateKey(key, StringUtils.join(default_val,"\n" ) );
                retval = default_val;
            }else{
                retval = val.split("\n");
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return retval;
    }

    public static  String getCookie(){
        return loadConfigFromDb(TABLE_KEY_SESSION,SESSION_COOKIENAME);
    }

    public static String[] getBaseUrls(){
        return loadConfigFromDb(TABLE_KEY_URLS,BASE_URLS);
    }

    public static String[] getFilters(){
        return loadConfigFromDb(TABLE_KEY_FILTER,RECORD_FILTER);
    }

    public static void addBaseUrl(String url) {
        SettingDb db = new SettingDb();
        String urls = "";
        try {
            String val = new SettingDb().loadSettings(Settings.TABLE_KEY_URLS);
            if(val == null){
                urls = url;
            }else{
                urls = val.trim() + "\n" + url;
            }
            db.updateKey(Settings.TABLE_KEY_URLS, urls);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

    }

    public static  boolean isShowTabSequence(){
        String val = loadConfigFromDb(TABLE_SHOW_TAB_SEQUENCE, String.valueOf(SHOW_TAB_SEQUENCE) );
        return Integer.parseInt(val) == 1;
    }

    public static  boolean isShowTabScript(){
        String val = loadConfigFromDb(TABLE_SHOW_TAB_SCRIPT, String.valueOf(SHOW_TAB_SCRIPT) );
        return Integer.parseInt(val) == 1;
    }
}
