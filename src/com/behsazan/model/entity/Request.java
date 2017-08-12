package com.behsazan.model.entity;

import burp.BurpExtender;
import burp.IHttpService;
import burp.IRequestInfo;
import burp.IResponseInfo;
import com.behsazan.model.DataUtils;

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
    private int id;
    private IRequestInfo analyzedRequest;
    private IResponseInfo analyzedResponse;
    private IHttpService httpService;

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
        analyzedResponse = null;
    }

    public void setRequest(byte[] request) {
        this.request = request;
        analyzedRequest = null;
    }

    @Override
    public String toString() {
        return getAnalysedRequest().getMethod() + "  " + getAnalysedRequest().getUrl().getPath();
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
        if(httpService == null){
            httpService = DataUtils.makeHttpService(url);
        }
        return httpService;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public IRequestInfo getAnalysedRequest() {
        if(analyzedRequest !=null){
            return analyzedRequest;
        }
        BurpExtender ext = BurpExtender.getInstance();
        analyzedRequest = ext.getHelpers().analyzeRequest(getHttpService(),request);
        return analyzedRequest;
    }

    public IResponseInfo getAnalysedResponse() {
        if(analyzedResponse !=null){
            return analyzedResponse;
        }
        BurpExtender ext = BurpExtender.getInstance();
        analyzedResponse = ext.getHelpers().analyzeResponse(response);
        return analyzedResponse;
    }
}
