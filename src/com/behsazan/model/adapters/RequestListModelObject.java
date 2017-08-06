package com.behsazan.model.adapters;

import burp.*;
import com.behsazan.model.DataUtils;
import com.behsazan.model.entity.Request;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by admin on 07/30/2017.
 */
public class RequestListModelObject {
//    private byte[] request;
//    private byte[] response;
    private IHttpService httpService;
    private String title;
    private IRequestInfo analysed;
    private Request requestObject;

    public RequestListModelObject(IInterceptedProxyMessage reqres) {
        this(reqres.getMessageInfo());
    }

    public RequestListModelObject(Request rq) {
        this.requestObject = rq;
//        this.request = rq.getRequest();
//        this.response = rq.getResponse();
        this.httpService = rq.getHttpService();
        BurpExtender ext = BurpExtender.getInstance();
        this.analysed = ext.getHelpers().analyzeRequest(httpService,requestObject.getRequest());
        this.title = analysed.getMethod() + "  " + analysed.getUrl().getPath();
    }

    public RequestListModelObject(IHttpRequestResponse reqres) {
        URL url = null;
        try {
            url = new URL(reqres.getHttpService().getProtocol(), reqres.getHttpService().getHost(), String.valueOf(reqres.getHttpService().getPort()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.requestObject = new Request(url,reqres.getRequest(),reqres.getResponse(),-1 );
        this.httpService = reqres.getHttpService();
        this.analysed = BurpExtender.getInstance().getHelpers().analyzeRequest(reqres);
        this.title = analysed.getMethod() + "  " + analysed.getUrl().getPath();
    }

    @Override
    public String toString() {
        return title;
    }

    public IHttpService getHttpService() {
        return httpService;
    }

    public byte[] getRequest() {
        return requestObject.getRequest();
    }

    public byte[] getResponse() {
        return requestObject.getResponse();
    }

    public IRequestInfo getAnalysed() {
        return analysed;
    }

    public void setRequest(byte[] request) {
        BurpExtender ext = BurpExtender.getInstance();
        this.analysed = ext.getHelpers().analyzeRequest(httpService,request);
        this.title = analysed.getMethod() + "  " + analysed.getUrl().getPath();
    }

    public Request getRequestObject() {
        return requestObject;
    }
}
