package burp;

import com.behsazan.view.Interface;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class BurpExtender implements IBurpExtender {
    private static final String EXTENSION_NAME = "Tadarokat V1";
    private static BurpExtender instance = null;
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    PrintWriter stdout;

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        instance = this;
        this.callbacks = callbacks;
        helpers = callbacks.getHelpers();
        stdout = new PrintWriter(callbacks.getStdout(),true);
        callbacks.setExtensionName(EXTENSION_NAME);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                BurpExtender.this.callbacks.addSuiteTab(new Interface());
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
}
