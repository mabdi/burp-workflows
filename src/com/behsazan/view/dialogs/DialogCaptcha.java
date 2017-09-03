package com.behsazan.view.dialogs;

import burp.IResponseInfo;
import com.behsazan.controller.Controller;
import com.behsazan.model.DataUtils;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.Request;
import com.behsazan.view.UIUtils;
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
    private RequestListModelObject requestListModelObject;
    private Controller.OnCaptchaRefresh onRefresh;

    public DialogCaptcha() {
        super(false);
    }

    public String setData(RequestListModelObject obj, Controller.OnCaptchaRefresh onRefresh){
        this.requestListModelObject = obj;
        this.onRefresh = onRefresh;
        buildImg();
        setVisible(true);
        return getTextField().getText();
    }

    private void buildImg() {
        IResponseInfo response = requestListModelObject.getRequestObject().getAnalysedResponse();

        Request rq = requestListModelObject.getRequestObject();
        byte[] bodyBytes = new byte[rq.getResponse().length - response.getBodyOffset()];
        System.arraycopy(rq.getResponse(),response.getBodyOffset(),bodyBytes,0,bodyBytes.length);
        BufferedImage img = DataUtils.getImageObject(bodyBytes);

        this.img = img;
        JLabel picLabel = new JLabel(new ImageIcon(img));
        add(picLabel,BorderLayout.CENTER);
        pack();
    }

    @Override
    protected void initUI() {
        setTitle("Enter Captcha");
        setLayout(new BorderLayout());
        setLocationRelativeTo(getParentWindow());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        add(getControlls(), BorderLayout.SOUTH);
    }

    public JPanel getControlls() {
        if (controlls == null) {
            controlls = new JPanel();
            controlls.setLayout(new FlowLayout(FlowLayout.LEFT));
            controlls.add(new JLabel("Captcha: "));
            controlls.add(getTextField());
            JButton btnOK = new JButton("Ok");
            setAsDefaultButton(btnOK);
            btnOK.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dissmiss();
                }
            });
            controlls.add(btnOK);
            JButton btnRefresh = new JButton("Refresh");
            btnRefresh.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doRefresh();
                }
            });
            controlls.add(btnRefresh);
        }
        return controlls;
    }

    private void doRefresh() {
        requestListModelObject = Controller.makeHttpRequest(requestListModelObject.getHttpService(), requestListModelObject.getRequest());
        if(onRefresh!=null){
            onRefresh.onRefresh(requestListModelObject);
        }
        buildImg();
    }

    public JTextField getTextField() {
        if(textField == null){
            textField = new JTextField("",10);
            textField.setComponentPopupMenu(UIUtils.buildNewPopMenuCopyCutPaste());
        }
        return textField;
    }
}
