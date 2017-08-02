package com.behsazan.model;

import burp.BurpExtender;
import burp.IHttpRequestResponse;
import burp.IHttpService;
import com.behsazan.model.entity.Request;
import com.behsazan.model.settings.Settings;

import java.io.File;
import java.net.URL;

/**
 * Created by admin on 07/30/2017.
 */
public class DataUtils {
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
        int port = 80;
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
}
