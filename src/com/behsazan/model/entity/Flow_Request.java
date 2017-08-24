package com.behsazan.model.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 08/07/2017.
 */
public class Flow_Request {
    private List<ResponseOut> outputParams;
    private List<RequestIn> inputParams;
    private Request request;
    private byte[] modifiedRequest;
    private int id;
    private Flow_Sequence flowSequence;

    public Flow_Request() {
    }

    public Flow_Request(int id, Request request, List<RequestIn> inputParams, List<ResponseOut> outputParams, byte[] modifiedRequest) {
        this.id = id;
        this.request = request;
        this.inputParams = inputParams;
        this.outputParams = outputParams;
        for (RequestIn ri : inputParams) {
            ri.setFlow_request(this);
        }
        for (ResponseOut ro : outputParams) {
            ro.setFlow_request(this);
        }
        this.modifiedRequest = modifiedRequest;
    }

    public static Flow_Request getInstaceFromRequest(Request rq) {
        Flow_Request instance = new Flow_Request(-1,rq,new ArrayList<RequestIn>(),new ArrayList<ResponseOut>(),rq.getRequest());
        return instance;
    }

    public Flow_Sequence getFlowSequence() {
        return flowSequence;
    }

    public void setFlowSequence(Flow_Sequence flowSequence) {
        this.flowSequence = flowSequence;
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
        input.setFlow_request(this);
    }

    public void addOutputParam(ResponseOut output){
        outputParams.add(output);
        output.setFlow_request(this);
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
