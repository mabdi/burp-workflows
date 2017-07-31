package com.behsazan.view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by admin on 07/30/2017.
 */
public class UIUtils {
    public static void invokeInDispatchThreadIfNeeded(Runnable runnable) {
        if (EventQueue.isDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    public static void invokeNotInDispatchThreadIfNeeded(Runnable runnable) {
        if (EventQueue.isDispatchThread()) {
            SwingUtilities.invokeLater(runnable);
        } else {
            runnable.run();
        }
    }
}
