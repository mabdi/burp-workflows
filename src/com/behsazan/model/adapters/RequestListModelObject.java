package com.behsazan.model.adapters;

import burp.*;
import com.behsazan.model.entity.Request;
import com.behsazan.model.entity.Flow_Running;
import com.behsazan.model.entity.Flow_Request;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by admin on 07/30/2017.
 */
public class RequestListModelObject {
//    private byte[] request;
//    private byte[] response;
    private IHttpService httpService;
    private IRequestInfo analysed;
    private Request requestObject;
    private Flow_Running testInstance;
    private Flow_Request testRequest;

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
    }

    @Override
    public String toString() {
        return requestObject.toString();
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
        getRequestObject().setRequest(request);
//        BurpExtender ext = BurpExtender.getInstance();
//
//        this.analysed = ext.getHelpers().analyzeRequest(httpService,request);
//        this.title = analysed.getMethod() + "  " + analysed.getUrl().getPath();
    }

    public Request getRequestObject() {
        return requestObject;
    }

    public void setTestInstance(Flow_Running testInstance) {
        this.testInstance = testInstance;
    }

    public Flow_Running getTestInstance() {
        return testInstance;
    }

    public void setTestRequest(Flow_Request testRequest) {
        this.testRequest = testRequest;
    }

    public Flow_Request getTestRequest() {
        return testRequest;
    }
}
