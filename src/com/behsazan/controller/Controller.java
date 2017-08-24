package com.behsazan.controller;

import burp.BurpExtender;
import burp.IHttpService;
import com.behsazan.model.DataUtils;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.*;
import com.behsazan.model.settings.Settings;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 08/24/2017.
 */
public class Controller {

    public static List<TestCaseInstance> buildTestCaseInstances(TestCase tcase,BuildTestCaseInstancesListener listener){
        List<TestCaseInstance> testCaseInstances = new ArrayList<>();
        List<Map<Integer,String>> pars = new ArrayList<>();
        pars.add(new HashMap<Integer,String>());
        for (TestCase_Sequence seq : tcase.getSeqs()) {
            for (TestCase_Request req : seq.getRequests()) {
                for (RequestIn inp : req.getInputParams()) {
                    List<Map<Integer,String>> newPars = new ArrayList<>();
                    for(int i=0;i<inp.getTxtValueLines().length;i++){
                        if(i==0){
                            for (Map<Integer,String> maps: pars) {
                                maps.put(inp.getId(),inp.getTxtValueLines()[0]);
                            }
                        }else{
                            for (Map<Integer,String> maps: pars) {
                                HashMap<Integer, String> hashMap = new HashMap<>(maps);
                                hashMap.put(inp.getId(),inp.getTxtValueLines()[i]);
                                newPars.add(hashMap);
                            }
                        }
                    }
                    for(Map<Integer,String> maps: newPars){
                        pars.add(maps);
                    }
                }
            }
        }
        int order = 0;
        for(Map<Integer,String> maps: pars){
            TestCaseInstance insta = new TestCaseInstance(tcase,maps, order);
            testCaseInstances.add(insta);
            if(listener!=null){
                listener.publishInstance(insta);
            }
        }
        return testCaseInstances;
    }

    public static List<RequestListModelObject> runTestCase(Component parent, TestCaseInstance instance, RunTestCaseListener listener){
        List<RequestListModelObject> requests = new ArrayList<>();
        for(TestCase_Sequence seq : instance.getTestCase().getSeqs()){
            if(listener != null && listener.isRunFinished()){
                break;
            }
            List<TestCase_Request> reqs = seq.getRequests();
            String base1 = seq.getBase1();
            String base2 = seq.getBase2();
            String cookie = seq.getCookie();
            URL url = seq.getUrl();
            for (TestCase_Request req: reqs) {
                if(listener != null && listener.isRunFinished()){
                    break;
                }
                List<RequestIn> inPars = req.getInputParams();
                List<ResponseOut> outPars = req.getOutputParams();
                byte[] modReq = req.getModifiedRequest();
                String[] msg = DataUtils.ExplodeRequest(modReq);
                msg = DataUtils.changeHost(msg,url.toString());
                msg = DataUtils.changeReferer(msg,url.toString());
                msg = DataUtils.changeUrlBase(msg,base1,base2);
                if(!cookie.isEmpty()) {
                    msg = DataUtils.changeCookie(msg, TestCaseInstance.queryGlobalVariable(cookie) );
                }
                for (RequestIn inPar : inPars) {
                    msg = DataUtils.applyParameter(msg,inPar,instance.getInitParamFor(inPar));
                }
                byte[] newRequest = DataUtils.buildRequest(msg);
                RequestListModelObject obj = makeHttpRequestAndWait(url,newRequest);
                obj.setTestInstance(instance);
                obj.setTestRequest(req);
                for (ResponseOut outPar : outPars) {
                    DataUtils.setOutParameters(parent,obj,outPar,instance);
                }
                instance.getRequestModelItem().add(obj);
                if(listener != null) {
                    listener.publishState(obj);
                    requests.add(obj);
                }
            }
        }
        return requests;

    }


    public static RequestListModelObject makeHttpRequestAndWait(URL url, byte[] newRequest){
        try {
            Thread.sleep(Settings.DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        IHttpService httpService = DataUtils.makeHttpService(url);
        return makeHttpRequest(httpService,newRequest);
    }

    public static RequestListModelObject makeHttpRequest(IHttpService httpService, byte[] newRequest){
        return new RequestListModelObject(BurpExtender.getInstance().getCallbacks().makeHttpRequest(httpService,newRequest));
    }

    public interface RunTestCaseListener {
        boolean isRunFinished();
        void publishState(RequestListModelObject state);
    }
    public interface BuildTestCaseInstancesListener{
        void publishInstance(TestCaseInstance instance);
    }
}
