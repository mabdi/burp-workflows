package com.behsazan.model;

import burp.BurpExtender;
import burp.IHttpRequestResponse;
import com.behsazan.model.settings.Settings;

/**
 * Created by admin on 07/30/2017.
 */
public class DataUtils {
    public static boolean isInTadarokatScope(IHttpRequestResponse reqres){
        return reqres.getHttpService().getProtocol().equalsIgnoreCase(Settings.TADAROKAT_protocol)  &&
                reqres.getHttpService().getHost().equalsIgnoreCase( Settings.TADAROKAT_HOST.toLowerCase()) &&
                reqres.getHttpService().getPort() == Settings.TADAROKAT_PORT;
    }
}
