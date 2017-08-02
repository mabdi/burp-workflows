package com.behsazan.model.adapters;

import burp.*;
import com.behsazan.model.entity.Request;

import java.net.URL;

/**
 * Created by admin on 07/30/2017.
 */
public class RequestListModelObject {
    private byte[] request;
    private byte[] response;
    private IHttpService httpService;
    private String title;
    private IRequestInfo analysed;

    public RequestListModelObject(IInterceptedProxyMessage reqres) {
        this(reqres.getMessageInfo());
    }

    public RequestListModelObject(Request rq) {
        this.request = rq.getRequest();
        this.response = rq.getResponse();
        this.httpService = rq.getHttpService();
        BurpExtender ext = BurpExtender.getInstance();
        this.analysed = ext.getHelpers().analyzeRequest(httpService,request);
        this.title = analysed.getMethod() + "  " + analysed.getUrl().getPath();
    }

    public RequestListModelObject(IHttpRequestResponse reqres) {
        this.request = reqres.getRequest();
        this.response = reqres.getResponse();
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
        return request;
    }

    public byte[] getResponse() {
        return response;
    }

    public IRequestInfo getAnalysed() {
        return analysed;
    }

    public void setRequest(byte[] request) {
        this.request = request;
        BurpExtender ext = BurpExtender.getInstance();
        this.analysed = ext.getHelpers().analyzeRequest(httpService,request);
        this.title = analysed.getMethod() + "  " + analysed.getUrl().getPath();
    }
}
