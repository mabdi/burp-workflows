package com.behsazan.controller;

import burp.BurpExtender;
import burp.IHttpService;
import com.behsazan.model.DataUtils;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.*;
import com.behsazan.model.settings.Settings;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by admin on 08/24/2017.
 */
public class Controller {

    public static List<Flow_Running> buildTestCaseInstances(Flow tcase, String baseUrl , Map<String,String[]> params,  BuildTestCaseInstancesListener listener){
        List<Flow_Running> testCaseInstances = new ArrayList<>();
        Map<String ,String> maps = new HashMap<>();
        List<Map<String,String>> pars = new ArrayList<>();
        permutation(pars,params,maps);
        for (Map<String,String> ins: pars) {
            Flow_Running insta = new Flow_Running(tcase,baseUrl,ins,0);
            testCaseInstances.add(insta);
            if(listener!=null){
                listener.publishInstance(insta);
            }
        }
        return testCaseInstances;
    }

    private static void permutation(List<Map<String,String>> pars, Map<String,String[]> allParams, Map<String ,String> maps){
        if(maps.size() == allParams.size()){
            pars.add(maps);
        }else{
            for(String key: allParams.keySet()){
                if(!maps.containsKey(key)){
                    for (String p :
                         allParams.get(key)) {
                        HashMap<String,String > newMap = new HashMap<>(maps);
                        newMap.put(key,p);
                        permutation(pars,allParams,newMap);
                    }
                    break;
                }
            }
        }
    }

    public static List<RequestListModelObject> runTestCase(Flow_Running instance, final RunTestCaseListener listener){
        final List<RequestListModelObject> requests = new ArrayList<>();
        for(Flow_Sequence seq : instance.getFlow().getSeqs()){
            if(listener != null && listener.isRunFinished()){
                break;
            }
            List<Flow_Request> reqs = seq.getRequests();
            String oldBase = seq.getSequence().getUrl();
            String newBase = instance.getBaseUrl();
            String cookie = seq.getCookie();
            for (final Flow_Request req: reqs) {
                if(listener != null && listener.isRunFinished()){
                    break;
                }
                List<ResponseOut> outPars = req.getOutputParams();
                byte[] modReq = req.getModifiedRequest();
                String[] msg = DataUtils.ExplodeRequest(modReq);
                msg = DataUtils.changeUrlBase(msg,oldBase,newBase);
                if(!cookie.isEmpty()) {
                    msg = DataUtils.changeCookie(msg, cookie );
                }
                msg = DataUtils.applyParameter(msg,instance);
                byte[] newRequest = DataUtils.buildRequest(msg);
                final RequestListModelObject obj = makeHttpRequestAndWait(newBase,newRequest);
                obj.setTestInstance(instance);
                obj.setTestRequest(req);
                for (ResponseOut outPar : outPars) {
                    DataUtils.setOutParameters(obj,outPar,instance,new OnCaptchaRefresh(){
                        @Override
                        public void onRefresh(RequestListModelObject requestListModelObject) {
                            if(listener != null) {
                                listener.publishState(requestListModelObject);
                                requests.add(requestListModelObject);
                            }
                        }
                    });
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

    public static RequestListModelObject makeHttpRequestAndWait(String strUrl, byte[] newRequest){
        try {
            URL url = new URL(strUrl);
            return makeHttpRequestAndWait(url,newRequest);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RequestListModelObject makeHttpRequestAndWait(URL url, byte[] newRequest){
        try {
            Thread.sleep(Settings.getDelay());
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
        void publishInstance(Flow_Running instance);
    }
    public interface OnCaptchaRefresh{
        void onRefresh(RequestListModelObject requestListModelObject);
    }
}
