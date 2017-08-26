package com.behsazan.model;

import burp.*;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.*;
import com.behsazan.model.settings.Settings;
import com.behsazan.view.dialogs.DialogCaptcha;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 07/30/2017.
 */
public class DataUtils {
    public static final String CRLF = "\r\n";

    public static String getAppHome() {
        String home = System.getProperty("user.home");
        String appHome = home + "\\TadarokatBurp";
        new File(appHome).mkdirs();
        return appHome;
    }

    public static IHttpService makeHttpService(String newroot) {
        try {
            URL url = new URL(newroot);
            return makeHttpService(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static IHttpService makeHttpService(URL newroot) {
        int port = newroot.getPort();
        if(newroot.getPort()==-1){
            if(newroot.getProtocol().equalsIgnoreCase("https")){
                port=443;
            }else{
                port=80;
            }
        }
        return BurpExtender.getInstance().getHelpers().buildHttpService(newroot.getHost(),port,newroot.getProtocol().equalsIgnoreCase("https"));
    }

    public static String baseFromUrl(IHttpService url){
        String prt = "";
        if(url.getPort()!=-1 && url.getPort()!= 80 && url.getPort()!= 443){
            prt = ":" + url.getPort();
        }
        String hostStr = url.getHost() + prt;
        return url.getProtocol() + "://" + hostStr + "/";
    }

    public static String getRootAddress(Request request) {
        IHttpService h = request.getHttpService();
        String port = "";
        if(h.getPort()!=80 && h.getPort()!= 443){
            port = ":" + h.getPort();
        }
        return h.getProtocol()+"://"+h.getHost()+port+"/";
    }

    public static String getBasePath(Request request) {
        String line1 = request.getAnalysedRequest().getHeaders().get(0);
        String[] line1parts = line1.split(" ");
        String[] pathes = line1parts[1].split("/");
        return "/" + ((pathes.length>1)? pathes[1]:"");
    }

    public static String getCookie(Request request) {
        List<IParameter> hds = request.getAnalysedRequest().getParameters();
        for (IParameter hd :
                hds) {
            if (hd.getType() == IParameter.PARAM_COOKIE && hd.getName().equals("JSESSIONID")) {
                return hd.getValue();
            }
        }
        return "";
    }

    public static byte[] changeCookie(Request request, final String newCookie){
        List<IParameter> hds = request.getAnalysedRequest().getParameters();
        for (final IParameter hd :
                hds) {
            if (hd.getType() == IParameter.PARAM_COOKIE && hd.getName().equals("JSESSIONID")) {
                return BurpExtender.getInstance().getHelpers().updateParameter(request.getRequest(), new IParameter() {
                    @Override
                    public byte getType() {
                        return IParameter.PARAM_COOKIE;
                    }

                    @Override
                    public String getName() {
                        return "JSESSIONID";
                    }

                    @Override
                    public String getValue() {
                        return newCookie;
                    }

                    @Override
                    public int getNameStart() {
                        return hd.getNameStart();
                    }

                    @Override
                    public int getNameEnd() {
                        return hd.getNameEnd();
                    }

                    @Override
                    public int getValueStart() {
                        return hd.getValueStart();
                    }

                    @Override
                    public int getValueEnd() {
                        return hd.getValueEnd();
                    }
                });
            }
        }
        return request.getRequest();
    }

    public static String[] changeCookie(String[] msg,String newCookie){
        for (int i=0;i<msg.length;i++) {
            if (msg[i].trim().isEmpty()) {
                break;
            }
            String hd = msg[i].trim();
            if (!newCookie.isEmpty()) {
                if (hd.startsWith("Cookie: ")) {
                    Pattern pattern = Pattern.compile("JSESSIONID=([^;]+)");
                    Matcher matcher = pattern.matcher(hd);
                    if(matcher.find()){
                        String oldCookie = matcher.group(1);
                        msg[i] = hd.replace(oldCookie, newCookie);
                    }

                }
            }
        }
        return msg;
    }

    public static String[] changeReferer(String[] msg, String newHost) {
        for (int i=0;i<msg.length;i++) {
            if (msg[i].trim().isEmpty()) {
                break;
            }
            String hd = msg[i].trim();
            if (!newHost.isEmpty()) {
                if (hd.startsWith("Referer: ")) {
                    URL urlNew = null;
                    URL urlOld = null;
                    try {
                        urlNew = new URL(newHost);
                        urlOld = new URL(hd.substring("Referer: ".length()).trim());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    msg[i] = "Referer: " + (urlNew.toString() + urlOld.getFile().substring(1));
                }
            }
        }
        return msg;
    }

    public static String[] changeHost(String[] msg, String newHost) {
        for (int i=0;i<msg.length;i++) {
            if (msg[i].trim().isEmpty()) {
                break;
            }
            String hd = msg[i].trim();
            if (!newHost.isEmpty()) {
                if (hd.startsWith("Host: ")) {
                    URL url = null;
                    try {
                        url = new URL(newHost);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    String prt = "";
                    if(url.getPort()!=-1 && url.getPort()!= 80 && url.getPort()!= 443){
                        prt = ":" + url.getPort();
                    }
                    String newHostStr = url.getHost() + prt;
                    msg[i] = "Host: " + newHostStr;
                }
            }
        }
        return msg;
    }

    public static String[] changeUrlBase(String[] msg,String oldBase,String newBase){
        if (!oldBase.equals(newBase)) {
            oldBase = (oldBase.trim().startsWith("/"))?oldBase.substring(1).trim():oldBase.trim();
            newBase = (newBase.trim().startsWith("/"))?newBase.substring(1).trim():newBase.trim();
            String line1 = msg[0].trim();
            String[] line1parts = line1.split(" ");
            String[] pathes = line1parts[1].split("/");
            if(pathes.length>1 && pathes[1].equals(oldBase.trim())){
                pathes[1] = newBase;
                line1parts[1] = StringUtils.join(pathes,"/");
                msg[0] = StringUtils.join(line1parts," ");
            }
        }
        return msg;
    }

    public static byte[] buildRequest(String[] msg){
        boolean emptyline = false;
        for (String s:msg) {
            if(s.trim().isEmpty()){
                emptyline = true;
            }
        }
        if(!emptyline){
            String[] msg2 = new String[msg.length + 1];
            for (int i = 0; i < msg.length; i++) {
                msg2[i] = msg[i];
            }
            msg2[msg2.length-1] = "";
        }
        String data = StringUtils.join(msg,CRLF);
        byte[] bytes = BurpExtender.getInstance().getHelpers().stringToBytes(data);
        return bytes;
    }


    public static String[] ExplodeRequest(byte[] request) {
        return BurpExtender.getInstance().getHelpers().bytesToString(request).split(CRLF,-1);
    }

    public static BufferedImage getImageObject(byte[] bytes){
        InputStream in = new ByteArrayInputStream(bytes);
        try {
            return ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] applyParameter(String[] msg, RequestIn inPar, String value) {
        if(value==null || value.isEmpty()){
            return msg;
        }
        for (int i=0;i<msg.length;i++) {
            String line = msg[i];
            if (line.contains(inPar.getPlaceHoder())) {
                msg[i] = line.replace(inPar.getPlaceHoder(),value);
            }
        }
        return msg;
    }

    public static void setOutParameters(RequestListModelObject obj, ResponseOut outPar, Flow_Running instance) {
        Request rq = obj.getRequestObject();
        IResponseInfo response = obj.getRequestObject().getAnalysedResponse();
        if(outPar.getType() == ResponseOut.TYPE_CAPTCHA){
            String captcha = showCaptcha(obj);
            if(outPar.isGlobal()) {
                instance.updateGlobalVariable(outPar.getName(),captcha);
            } else {
                instance.updateLocalVariable(outPar.getName(),captcha);
            }
            return;
        }
        if(outPar.getType() == ResponseOut.TYPE_COOKIE){
            for (ICookie coo :response.getCookies()) {
                if(coo.getName().equals(Settings.SESSION_COOKIENAME)){
                    if(outPar.isGlobal()) {
                        instance.updateGlobalVariable(outPar.getName(),coo.getValue());
                    } else {
                        instance.updateLocalVariable(outPar.getName(),coo.getValue());
                    }
                    return;
                }
            }
            return;
        }
        if(outPar.getType() == ResponseOut.TYPE_REGEX){
            String allres = new String(rq.getResponse(), Charset.forName("UTF-8"));
            Pattern p = Pattern.compile(outPar.getParam());
            Matcher m = p.matcher(allres);
            if(m.find()) {
                if(outPar.isGlobal()) {
                    instance.updateGlobalVariable(outPar.getName(),m.group());
                } else {
                    instance.updateLocalVariable(outPar.getName(),m.group());
                }
            }
            return;
        }

        byte[] bodyBytes = new byte[rq.getResponse().length - response.getBodyOffset()];
        System.arraycopy(rq.getResponse(),response.getBodyOffset(),bodyBytes,0,bodyBytes.length);
        String body = new String(bodyBytes, Charset.forName("UTF-8"));
        Document htmlDoc = Jsoup.parse(body);
        if(outPar.getType() == ResponseOut.TYPE_HIDDEN){
            Elements hidden = htmlDoc.getElementsByTag("input[name=\""+outPar.getParam().replaceAll("\"","\\\"")+"\"]");
            if(outPar.isGlobal()) {
                instance.updateGlobalVariable(outPar.getName(),hidden.val());
            } else {
                instance.updateLocalVariable(outPar.getName(),hidden.val());
            }
            return;
        }
        if(outPar.getType() == ResponseOut.TYPE_CSS){
            Elements res = htmlDoc.select(outPar.getParam());
            // TODO get a second parameter for attribute of element, after css selector
            String val = res.val();
            if(val.isEmpty()){
                val = res.text();
            }
            if(val.isEmpty()){
                val = res.html();
            }
            if(outPar.isGlobal()) {
                instance.updateGlobalVariable(outPar.getName(),val);
            } else {
                instance.updateLocalVariable(outPar.getName(),val);
            }
            return;
        }
    }

    private static String showCaptcha(RequestListModelObject obj) {
        IResponseInfo response = obj.getRequestObject().getAnalysedResponse();

        Request rq = obj.getRequestObject();
        byte[] bodyBytes = new byte[rq.getResponse().length - response.getBodyOffset()];
        System.arraycopy(rq.getResponse(),response.getBodyOffset(),bodyBytes,0,bodyBytes.length);
        BufferedImage img = getImageObject(bodyBytes);
        DialogCaptcha dlg = new DialogCaptcha();
        return dlg.setData(img);
    }

    public static void exportFlow(int id, File file) {
        exportFlow(new int[]{id},file);
    }

    public static void exportFlow(int[] ids, File file) {
        Flow[] flows = new Flow[ids.length];
        for (int i = 0; i < ids.length; i++) {
            flows[i] = Flow.getById(ids[i]);
        }
        exportFlow(flows,file);
    }

    private static void exportFlow(Flow[] flows, File file) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(flows);
        try {
            FileUtils.write(file,json,"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isValidURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
