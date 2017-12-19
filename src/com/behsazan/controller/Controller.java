package com.behsazan.controller;

import burp.BurpExtender;
import burp.IHttpService;
import com.behsazan.model.DataUtils;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.*;
import com.behsazan.model.settings.Settings;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // injection point -- test start
        for(Script script: instance.getFlow().getScripts()){
            if(script.getType() == Script.TYPE_ON_TEST_START){
                inject_script(script,instance);
            }
        }
        for(Flow_Sequence seq : instance.getFlow().getSeqs()){

            if(listener != null && listener.isRunFinished()){
                break;
            }
            // injection point -- sequence start
            for(Script script: instance.getFlow().getScripts()){
                if(script.getType() == Script.TYPE_ON_SEQUENCE_START){
                    inject_script(script,instance, seq);
                }
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
                // injection point -- before request modify
                for(Script script: instance.getFlow().getScripts()){
                    if(script.getType() == Script.TYPE_ON_REQUEST_BEFORE_ASSIGNMENT){
                        Object res = inject_script(script, instance, seq, req, modReq, msg);
                        if(res != null && res instanceof String[]){
                            msg = (String[])res;
                        }
                    }
                }
                msg = DataUtils.changeUrlBase(msg,oldBase,newBase);
                if(!cookie.isEmpty()) {
                    msg = DataUtils.changeCookie(msg, cookie);
                }
                msg = DataUtils.applyParameter(msg,instance);
                // injection point -- after request modify
                for(Script script: instance.getFlow().getScripts()){
                    if(script.getType() == Script.TYPE_ON_REQUEST_AFTER_ASSIGNMENT){
                        Object res = inject_script(script, instance, seq, req, modReq, msg);
                        if(res != null && res instanceof String[]){
                            msg = (String[])res;
                        }
                    }
                }

                byte[] newRequest = DataUtils.buildRequest(msg);
                final RequestListModelObject obj = makeHttpRequestAndWait(newBase,newRequest);
                obj.setTestInstance(instance);
                obj.setTestRequest(req);
                // injection point -- on response
                for(Script script: instance.getFlow().getScripts()){
                    if(script.getType() == Script.TYPE_ON_RESPONSE_RECEIVED){
                        inject_script(script, instance, seq, req, newRequest, obj);
                    }
                }
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
                instance.addItemToRequests(obj);
                if(listener != null) {
                    listener.publishState(obj);
                    requests.add(obj);
                }
            }
            // injection point -- sequence finished
            // injection point -- on response
            for(Script script: instance.getFlow().getScripts()){
                if(script.getType() == Script.TYPE_ON_SEQUENCE_FINISH){
                    inject_script(script, instance,seq);
                }
            }
        }
        // injection point -- test finished
        // injection point -- on response
        for(Script script: instance.getFlow().getScripts()){
            if(script.getType() == Script.TYPE_ON_TEST_FINISH){
                inject_script(script, instance);
            }
        }
        return requests;
    }

    private static Object inject_script(Script script,Object... params){
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            String engineLang = "JavaScript";
            switch (script.getLang()){
                case Script.LANG_PYTHON:
                    engineLang = "python";
                    break;
                case Script.LANG_RUBY:
                    engineLang = "ruby";
                    break;
            }
            ScriptEngine engine = manager.getEngineByName(engineLang);
            engine.eval(script.getText());

            Invocable invokable = (Invocable) engine;
            Object result = invokable.invokeFunction("enbale",params);
            if((Boolean) result){
                return invokable.invokeFunction("action",params);
            }
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
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
