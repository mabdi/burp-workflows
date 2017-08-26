package com.behsazan.view.dialogs;

import com.behsazan.view.abstracts.AbstractDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Created by admin on 08/19/2017.
 */
public class DialogCaptcha extends AbstractDialog {

    private JPanel controlls;
    private JTextField textField;
    private BufferedImage img;

    public DialogCaptcha() {
        super(false);
    }

    public String setData(BufferedImage img){
        this.img = img;
        JLabel picLabel = new JLabel(new ImageIcon(img));
        add(picLabel,BorderLayout.CENTER);
        pack();
        setVisible(true);
        return getTextField().getText();
    }

    @Override
    protected void initUI() {
        setTitle("Enter Captcha");
        setLayout(new BorderLayout());
        setLocationRelativeTo(getParentWindow());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        add(getControlls(), BorderLayout.SOUTH);
        // TODO add a refresh Captcha button
    }

    public JPanel getControlls() {
        if (controlls == null) {
            controlls = new JPanel();
            controlls.setLayout(new BorderLayout());
            controlls.add(new JLabel("Captcha: "),BorderLayout.WEST);
            controlls.add(getTextField(),BorderLayout.CENTER);
            JButton btnOK = new JButton("Ok");
            btnOK.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dissmiss();
                }
            });
            controlls.add(btnOK,BorderLayout.EAST);
        }
        return controlls;
    }

    public JTextField getTextField() {
        if(textField == null){
            textField = new JTextField("",10);
        }
        return textField;
    }
}
