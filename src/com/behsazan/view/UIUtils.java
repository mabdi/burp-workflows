package com.behsazan.view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by admin on 07/30/2017.
 */
public class UIUtils {
    public static void invokeInDispatchThreadIfNeeded(Runnable runnable) {
        invokeInDispatchThread(runnable,true);
    }

    public static void invokeNotInDispatchThreadIfNeeded(Runnable runnable) {
        invokeInDispatchThread(runnable,false);
    }

    private static void invokeInDispatchThread(Runnable runnable, boolean inDispatch) {
        if(inDispatch){
            if (EventQueue.isDispatchThread()) {
                SwingUtilities.invokeLater(runnable);
            } else {
                runnable.run();
            }
        }else {
            if (EventQueue.isDispatchThread()) {
                runnable.run();
            } else {
                SwingUtilities.invokeLater(runnable);
            }
        }
    }
}
