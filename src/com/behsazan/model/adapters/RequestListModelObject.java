package com.behsazan.model.adapters;

import burp.*;

/**
 * Created by admin on 07/30/2017.
 */
public class RequestListModelObject {
    private final IInterceptedProxyMessage reqres;
    private final String title;
    private final IRequestInfo analysed;

    public RequestListModelObject(IInterceptedProxyMessage reqres) {
        this.reqres = reqres;
        this.analysed = BurpExtender.getInstance().getHelpers().analyzeRequest(reqres.getMessageInfo());
        this.title = analysed.getMethod() + "  " + analysed.getUrl().getPath();
    }

    @Override
    public String toString() {
        return title;
    }

    public IHttpService getHttpService() {
        return reqres.getMessageInfo().getHttpService();
    }

    public byte[] getRequest() {
        return reqres.getMessageInfo().getRequest();
    }

    public byte[] getResponse() {
        return reqres.getMessageInfo().getResponse();
    }
}
