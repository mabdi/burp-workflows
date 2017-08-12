package com.behsazan.model.entity;

import burp.IResponseInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 08/07/2017.
 */
public class TestCase_Request {
    private final ArrayList<ResponseOut> outputParams;
    private List<RequestIn> inputParams;
    private Document htmlDoc;
    private Elements htmlforms;
    private Request request;
    private byte[] modifiedRequest;
    private int id;

    public TestCase_Request(Request rq) {
        this.request = rq;
        inputParams = new ArrayList<>();
        outputParams = new ArrayList<>();
        rq.getAnalysedRequest();
        rq.getAnalysedResponse();
//        initResponseOutForms(rq);
        setModifiedRequest(rq.getRequest());
    }

    private void initResponseOutForms(Request rq) {
        IResponseInfo response = rq.getAnalysedResponse();
        if(!response.getInferredMimeType().equalsIgnoreCase("text/html")){
            return;
        }
        byte[] bodyBytes = new byte[rq.getResponse().length - response.getBodyOffset()];
        System.arraycopy(rq.getResponse(),response.getBodyOffset(),bodyBytes,0,bodyBytes.length);
        String body = new String(bodyBytes, Charset.forName("UTF-8"));
        this.htmlDoc = Jsoup.parse(body);
        this.htmlforms = htmlDoc.getElementsByTag("form");

    }

    public void addInputParam(RequestIn input){
        inputParams.add(input);
    }

    public void addOutputParam(ResponseOut output){
        outputParams.add(output);
    }

    public ArrayList<ResponseOut> getOutputParams() {
        return outputParams;
    }

    public List<RequestIn> getInputParams() {
        return inputParams;
    }

    public Request getRequest() {
        return request;
    }

    @Override
    public String toString() {
        return request.toString();
    }

    public byte[] getModifiedRequest() {
        return modifiedRequest;
    }

    public void setModifiedRequest(byte[] modifiedRequest) {
        this.modifiedRequest = modifiedRequest;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
