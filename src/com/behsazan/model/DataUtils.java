package com.behsazan.model;

import burp.BurpExtender;
import burp.IHttpRequestResponse;
import burp.IHttpService;
import burp.IRequestInfo;
import com.behsazan.model.entity.Request;
import com.behsazan.model.settings.Settings;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 07/30/2017.
 */
public class DataUtils {
    public static final String CRLF = "\r\n";


    public static boolean isInTadarokatScope(IHttpRequestResponse reqres){
        return reqres.getHttpService().getProtocol().equalsIgnoreCase(Settings.TADAROKAT_protocol)  &&
                reqres.getHttpService().getHost().equalsIgnoreCase( Settings.TADAROKAT_HOST.toLowerCase()) &&
                reqres.getHttpService().getPort() == Settings.TADAROKAT_PORT;
    }

    public static String getAppHome() {
        String home = System.getProperty("user.home");
        String appHome = home + "\\TadarokatBurp";
        new File(appHome).mkdirs();
        return appHome;
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

    public static String getRootAddress(Request request) {
        IHttpService h = request.getHttpService();
        String port = "";
        if(h.getPort()!=80 && h.getPort()!= 443){
            port = ":" + h.getPort();
        }
        return h.getProtocol()+"://"+h.getHost()+port+"/";
    }

    public static String getBasePath(Request request) {
        String line1 = request.getAnalysed().getHeaders().get(0);
        String[] line1parts = line1.split(" ");
        String[] pathes = line1parts[1].split("/");
        return "/" + ((pathes.length>1)? pathes[1]:"");
    }

    public static String getCookie(Request request) {
        List<String> hds = request.getAnalysed().getHeaders();
        for (String hd :
                hds) {
            if (hd.startsWith("Cookie")) {
                Pattern pattern = Pattern.compile("JSESSIONID=([^;]+)");
                Matcher matcher = pattern.matcher(hd);
                if(matcher.find()){
                    return matcher.group(1);
                }
            }
            }
        return "";
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

}
