package com.behsazan.view;

import burp.BurpExtender;
import com.behsazan.controller.Controller;
import com.behsazan.view.dialogs.DialogWaiting;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import java.awt.*;
import java.awt.event.ActionEvent;

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


    public static void showGenerealError() {
        JOptionPane.showMessageDialog(BurpExtender.getUiParent(),"Error occurred.","Error",JOptionPane.ERROR_MESSAGE);
    }

    public static JPopupMenu buildNewPopMenuCopyCutPaste(){
        JPopupMenu menu = new JPopupMenu();
        Action cut = new DefaultEditorKit.CutAction();
        cut.putValue(Action.NAME, "Cut");
        cut.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
        menu.add( cut );

        Action copy = new DefaultEditorKit.CopyAction();
        copy.putValue(Action.NAME, "Copy");
        copy.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
        menu.add( copy );

        Action paste = new DefaultEditorKit.PasteAction();
        paste.putValue(Action.NAME, "Paste");
        paste.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));
        menu.add( paste );

        Action selectAll = new SelectAll();
        menu.add( selectAll );

        Action delete = new DeleteAction();
        menu.add( delete );

        return menu;
    }

    public static class SelectAll extends TextAction
    {
        public SelectAll()
        {
            super("Select All");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control A"));
        }

        public void actionPerformed(ActionEvent e)
        {
            JTextComponent component = getFocusedComponent();
            component.selectAll();
            component.requestFocusInWindow();
        }
    }

    public static class DeleteAction extends TextAction
    {
        public DeleteAction()
        {
            super("Delete");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control delete"));
        }

        public void actionPerformed(ActionEvent e)
        {
            JTextComponent component = getFocusedComponent();
            String txt = component.getText();
            component.setText(txt.substring(0,component.getSelectionStart())+txt.substring(component.getSelectionEnd()));
            component.requestFocusInWindow();
        }
    }

    public static class FormUtility {
        /**
         * Grid bag constraints for fields and labels
         */
        private GridBagConstraints lastConstraints = null;
        private GridBagConstraints middleConstraints = null;
        private GridBagConstraints labelConstraints = null;

        public FormUtility() {
            // Set up the constraints for the "last" field in each
            // row first, then copy and modify those constraints.

            // weightx is 1.0 for fields, 0.0 for labels
            // gridwidth is REMAINDER for fields, 1 for labels
            lastConstraints = new GridBagConstraints();

            // Stretch components horizontally (but not vertically)
            lastConstraints.fill = GridBagConstraints.HORIZONTAL;

            // Components that are too short or narrow for their space
            // Should be pinned to the northwest (upper left) corner
            lastConstraints.anchor = GridBagConstraints.NORTHWEST;

            // Give the "last" component as much space as possible
            lastConstraints.weightx = 1.0;

            // Give the "last" component the remainder of the row
            lastConstraints.gridwidth = GridBagConstraints.REMAINDER;

            // Add a little padding
            lastConstraints.insets = new Insets(1, 1, 1, 1);

            // Now for the "middle" field components
            middleConstraints =
                    (GridBagConstraints) lastConstraints.clone();

            // These still get as much space as possible, but do
            // not close out a row
            middleConstraints.gridwidth = GridBagConstraints.RELATIVE;

            // And finally the "label" constrains, typically to be
            // used for the first component on each row

            labelConstraints =
                    (GridBagConstraints) lastConstraints.clone();
            labelConstraints.anchor = GridBagConstraints.NORTHWEST;
            // Give these as little space as necessary
            labelConstraints.weightx = 0.0;
            labelConstraints.gridwidth = 1;
        }

        /**
         * Adds a field component. Any component may be used. The
         * component will be stretched to take the remainder of
         * the current row.
         */
        public void addLastField(Component c, Container parent) {
            GridBagLayout gbl = (GridBagLayout) parent.getLayout();
            gbl.setConstraints(c, lastConstraints);
            parent.add(c);
        }

        /**
         * Adds an arbitrary label component, starting a new row
         * if appropriate. The width of the component will be set
         * to the minimum width of the widest component on the
         * form.
         */
        public void addLabel(Component c, Container parent) {
            GridBagLayout gbl = (GridBagLayout) parent.getLayout();
            gbl.setConstraints(c, labelConstraints);
            parent.add(c);
        }

        /**
         * Adds a JLabel with the given string to the label column
         */
        public JLabel addLabel(String s, Container parent) {
            JLabel c = new JLabel(s);
            addLabel(c, parent);
            return c;
        }

        /**
         * Adds a "middle" field component. Any component may be
         * used. The component will be stretched to take all of
         * the space between the label and the "last" field. All
         * "middle" fields in the layout will be the same width.
         */
        public void addMiddleField(Component c, Container parent) {
            GridBagLayout gbl = (GridBagLayout) parent.getLayout();
            gbl.setConstraints(c, middleConstraints);
            parent.add(c);
        }

//        public void addFiller(Container parent){
//            GridBagConstraints horizontalFill = new GridBagConstraints();
//            horizontalFill.anchor = GridBagConstraints.WEST;
//            horizontalFill.fill = GridBagConstraints.HORIZONTAL;
//            parent.add(Box.createHorizontalGlue(), horizontalFill);
//        }

    }
}
