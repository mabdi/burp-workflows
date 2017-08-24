package com.behsazan.model.entity;

import burp.BurpExtender;
import burp.IHttpService;
import burp.IRequestInfo;
import burp.IResponseInfo;
import com.behsazan.model.DataUtils;
import com.behsazan.model.sqlite.RequestDb;
import com.behsazan.model.sqlite.SqliteHelper;

import java.net.URL;
import java.sql.SQLException;

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

    public Request() {
    }

    public Request(URL url, byte[] request, byte[] response, int order) {
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

    public static void updateRequestRequest(int id, byte[] request) throws SQLException {
        new RequestDb().updateRequestRequest(id,request);
    }

    public static void updateRequestResponse(int id, byte[] response) throws SQLException {
        new RequestDb().updateRequestResponse(id,response);
    }

    public static Request getById(int request_id) throws SQLException {
        return new RequestDb().getRequestById(request_id);
    }
}
