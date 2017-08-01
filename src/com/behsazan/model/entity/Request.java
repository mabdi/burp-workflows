package com.behsazan.model.entity;

import burp.BurpExtender;
import burp.IHttpService;

import java.net.URL;

/**
 * Created by admin on 07/31/2017.
 */
public class Request {
    private int order;
    private Sequence sequence;
    private byte[] request;
    private byte[] response;
    private URL url;

    public Request(URL url, byte[] request,byte[] response, int order) {
        this.url = url;
        this.request = request;
        this.response = response;
        this.order = order;
    }



    public Sequence getSequence() {
        return sequence;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }

    public byte[] getRequest() {
        return request;
    }

    public byte[] getResponse() {
        return response;
    }

    public void setResponse(byte[] response) {
        this.response = response;
    }

    public void setRequest(byte[] request) {
        this.request = request;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public IHttpService getHttpService() {
        BurpExtender ext = BurpExtender.getInstance();
        IHttpService httpService = ext.getHelpers().buildHttpService(url.getHost(),url.getPort(),url.getProtocol());
        return httpService;
    }
}
