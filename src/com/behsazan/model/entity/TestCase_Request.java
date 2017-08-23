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
    private List<ResponseOut> outputParams;
    private List<RequestIn> inputParams;
    private Request request;
    private byte[] modifiedRequest;
    private int id;
    private TestCase_Sequence testCaseSequence;

    public TestCase_Request() {
    }

    public TestCase_Request(int id, Request request, List<RequestIn> inputParams, List<ResponseOut> outputParams, byte[] modifiedRequest) {
        this.id = id;
        this.request = request;
        this.inputParams = inputParams;
        this.outputParams = outputParams;
        for (RequestIn ri : inputParams) {
            ri.setTestCase_request(this);
        }
        for (ResponseOut ro : outputParams) {
            ro.setTestCase_request(this);
        }
        this.modifiedRequest = modifiedRequest;
    }

    public static TestCase_Request getInstaceFromRequest(Request rq) {
        TestCase_Request instance = new TestCase_Request(-1,rq,new ArrayList<RequestIn>(),new ArrayList<ResponseOut>(),rq.getRequest());
        return instance;
    }

    public TestCase_Sequence getTestCaseSequence() {
        return testCaseSequence;
    }

    public void setTestCaseSequence(TestCase_Sequence testCaseSequence) {
        this.testCaseSequence = testCaseSequence;
    }

//    private void initResponseOutForms(Request rq) {
//        IResponseInfo response = rq.getAnalysedResponse();
//        if(!response.getInferredMimeType().equalsIgnoreCase("text/html")){
//            return;
//        }
//        byte[] bodyBytes = new byte[rq.getResponse().length - response.getBodyOffset()];
//        System.arraycopy(rq.getResponse(),response.getBodyOffset(),bodyBytes,0,bodyBytes.length);
//        String body = new String(bodyBytes, Charset.forName("UTF-8"));
//        this.htmlDoc = Jsoup.parse(body);
//        this.htmlforms = htmlDoc.getElementsByTag("form");
//
//    }

    public void addInputParam(RequestIn input){
        inputParams.add(input);
        input.setTestCase_request(this);
    }

    public void addOutputParam(ResponseOut output){
        outputParams.add(output);
        output.setTestCase_request(this);
    }

    public List<ResponseOut> getOutputParams() {
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
