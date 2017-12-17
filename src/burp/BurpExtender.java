package burp;

import com.behsazan.model.sqlite.SqliteHelper;
import com.behsazan.view.Interface;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class BurpExtender implements IBurpExtender {
    private static final String EXTENSION_NAME = "Workfolws";
    private static BurpExtender instance = null;
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private static ITab UI_PARENT;
    PrintWriter stdout;

    public static Component getUiParent() {
        return UI_PARENT.getUiComponent();
    }

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        instance = this;
        this.callbacks = callbacks;
        helpers = callbacks.getHelpers();
        stdout = new PrintWriter(callbacks.getStdout(),true);
        callbacks.setExtensionName(EXTENSION_NAME);
        try {
            new SqliteHelper().initDb();
        } catch (SQLException e) {
            e.printStackTrace(stdout);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UI_PARENT = new Interface();
                BurpExtender.this.callbacks.addSuiteTab(UI_PARENT);
            }
        });

        stdout.println("Plugin registerd "+EXTENSION_NAME);
    }

    public PrintWriter getStdout() {
        return stdout;
    }

    public IExtensionHelpers getHelpers() {
        return helpers;
    }

    public IBurpExtenderCallbacks getCallbacks() {
        return callbacks;
    }

    public static String getExtensionName() {
        return EXTENSION_NAME;
    }

    public static BurpExtender getInstance() {
        return instance;
    }

    public static void logText(String s){
        getInstance().getStdout().println(s);
    }
}
