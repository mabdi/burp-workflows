package com.behsazan.model;

import burp.*;
import com.behsazan.controller.Controller;
import com.behsazan.controller.Flow_Running;
import com.behsazan.model.adapters.RequestListModelObject;
import com.behsazan.model.entity.Flow;
import com.behsazan.model.entity.Request;
import com.behsazan.model.entity.ResponseOut;
import com.behsazan.model.settings.Settings;
import com.behsazan.view.dialogs.DialogCaptcha;
import com.google.gson.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 07/30/2017.
 */
public class DataUtils {
    public static final String CRLF = "\r\n";

    public static String getAppHome() {
        String home = System.getProperty("user.home");
        String appHome = home + "\\WorkflowsBurp";
        new File(appHome).mkdirs();
        return appHome;
    }

    public static IHttpService makeHttpService(String newroot) {
        try {
            URL url = new URL(newroot);
            return makeHttpService(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static IHttpService makeHttpService(URL newroot) {
        int port = newroot.getPort();
        if(newroot.getPort()==-1){
            if(newroot.getProtocol().equalsIgnoreCase("https")){
                port=443;
            }else{
                port=80;
            }
        }
        return BurpExtender.getInstance().getHelpers().buildHttpService(newroot.getHost(),port,newroot.getProtocol().equalsIgnoreCase("https"));
    }

    public static String getRootAddress(Request request) {
        IHttpService h = request.getHttpService();
        String port = "";
        if(h.getPort()!=80 && h.getPort()!= 443){
            port = ":" + h.getPort();
        }
        return h.getProtocol() + "://" + h.getHost() + port;
    }

    public static String getBasePath(Request request) {
        String line1 = request.getAnalysedRequest().getHeaders().get(0);
        String[] line1parts = line1.split(" ");
        String[] pathes = line1parts[1].split("/");
        return "/" + ((pathes.length>1)? pathes[1]:"");
    }

    public static String getCookie(Request request) {
        List<IParameter> hds = request.getAnalysedRequest().getParameters();
        for (IParameter hd :
                hds) {
            if (hd.getType() == IParameter.PARAM_COOKIE && hd.getName().equals(Settings.getCookie())) {
                return hd.getValue();
            }
        }
        return "";
    }

    public static String[] changeCookie(String[] msg,String newCookie){
        for (int i=0;i<msg.length;i++) {
            if (msg[i].trim().isEmpty()) {
                break;
            }
            String hd = msg[i].trim();
            if (!newCookie.isEmpty()) {
                if (hd.startsWith("Cookie: ")) {
                    Pattern pattern = Pattern.compile(Settings.getCookie() + "=([^;]+)");
                    Matcher matcher = pattern.matcher(hd);
                    if(matcher.find()){
                        String oldCookie = matcher.group(1);
                        msg[i] = hd.replace(oldCookie, newCookie);
                    }
                }
            }
        }
        return msg;
    }

    private static String[] changeReferer(String[] msg, String newHost) {
        for (int i=0;i<msg.length;i++) {
            if (msg[i].trim().isEmpty()) {
                break;
            }
            String hd = msg[i].trim();
            if (!newHost.isEmpty()) {
                if (hd.startsWith("Referer: ")) {
                    URL urlNew = null;
                    URL urlOld = null;
                    try {
                        urlNew = new URL(newHost);
                        urlOld = new URL(hd.substring("Referer: ".length()).trim());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    // TODO: It seems it's logic is incorrect. debug it in future.
                    msg[i] = "Referer: " + (urlNew.toString() + urlOld.getFile().substring(1));
                }
            }
        }
        return msg;
    }


    public static String[] setContentLength(String[] msg) {
        for (int i = 0; i < msg.length; i++) {
            if (msg[i].trim().isEmpty()) {
                break;
            }
            String hd = msg[i].trim();
            if (hd.startsWith("Content-Length: ")) {
                msg[i] = "Content-Length: " + calculateContentLength(msg);
            }
        }
        return msg;
    }

    private static int calculateContentLength(String[] msg) {
        boolean isblankLinePassed = false;
        int res = 0;
        for (int i = 0; i < msg.length; i++) {
            if (msg[i].trim().isEmpty()) {
                isblankLinePassed = true;
                continue;
            }
            if (isblankLinePassed) {
                res += msg[i].getBytes().length;
            }
        }
        return res;
    }

    private static String[] changeHost(String[] msg, String newHost) {
        for (int i=0;i<msg.length;i++) {
            if (msg[i].trim().isEmpty()) {
                break;
            }
            String hd = msg[i].trim();
            if (!newHost.isEmpty()) {
                if (hd.startsWith("Host: ")) {
                    msg[i] = "Host: " + hostHeaderFromUrl(newHost);
                }
            }
        }
        return msg;
    }

    private static String hostHeaderFromUrl(String newHost){
        URL url = null;
        try {
            url = new URL(newHost);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String prt = "";
        if(url.getPort()!=-1 && url.getPort()!= 80 && url.getPort()!= 443){
            prt = ":" + url.getPort();
        }
        return url.getHost() + prt;
    }

    public static String[] changeUrlBase(String[] msg,String oldBase,String newBase){
        newBase = (newBase.endsWith("/"))?newBase.substring(0,newBase.length()-1):newBase;
        oldBase = (oldBase.endsWith("/"))?oldBase.substring(0,oldBase.length()-1):oldBase;
        if (!oldBase.equals(newBase)) {
            msg = DataUtils.changeHost(msg,newBase.toString());
            msg = DataUtils.changeReferer(msg,newBase.toString());
            URL urlOld = null,urlNew = null;
            try {
                urlOld = new URL(oldBase);
                urlNew = new URL(newBase);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            String line1 = msg[0].trim();
            String[] line1parts = line1.split(" ");
            if(!urlOld.getPath().isEmpty()) {
                line1parts[1] = line1parts[1].replace(urlOld.getPath(), urlNew.getPath());
            }else{
                line1parts[1] = urlNew.getPath() + line1parts[1];
            }
            msg[0] = StringUtils.join(line1parts," ");
//            String[] pathes = line1parts[1].split("/");
//            if(pathes.length>1 && pathes[1].equals(oldBase.trim())){
//                pathes[1] = newBase;
//                line1parts[1] = StringUtils.join(pathes,"/");
//                msg[0] = StringUtils.join(line1parts," ");
//            }
        }
        return msg;
    }

    public static byte[] buildRequest(String[] msg){
        boolean emptyline = false;
        for (String s:msg) {
            if(s.trim().isEmpty()){
                emptyline = true;
            }
        }
        if(!emptyline){
            String[] msg2 = new String[msg.length + 1];
            for (int i = 0; i < msg.length; i++) {
                msg2[i] = msg[i];
            }
            msg2[msg2.length-1] = "";
        }
        String data = StringUtils.join(msg,CRLF);
        byte[] bytes = BurpExtender.getInstance().getHelpers().stringToBytes(data);
        return bytes;
    }


    public static String[] ExplodeRequest(byte[] request) {
        return BurpExtender.getInstance().getHelpers().bytesToString(request).split(CRLF,-1);
    }

    public static BufferedImage getImageObject(byte[] bytes){
        InputStream in = new ByteArrayInputStream(bytes);
        try {
            return ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String[] applyParameter(String[] msg, Flow_Running instance) {
        for (int i=0;i<msg.length;i++) {
            String line = msg[i];
            line = applyLineParam(line,Pattern.compile(Settings.PARAM_PATTERN),instance.getParams());
            line = applyLineParam(line,Pattern.compile(Settings.LOCAL_PATTERN),instance.getLocals());
            msg[i] = applyLineParam(line,Pattern.compile(Settings.GLOBAL_PATTERN),instance.getGLOBALS());
        }
        return msg;
    }

    public static String applyLineParam(String line, Pattern pattern, Map<String, String> mem) {
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()){
            String placeHodler = matcher.group(0);
            String varname = matcher.group(1);
            if(mem.containsKey(varname)){
                line = line.replace(placeHodler,mem.get(varname));
            }
        }
        return  line;
    }

    public static void setOutParameters(RequestListModelObject obj, ResponseOut outPar, Flow_Running instance, Controller.OnCaptchaRefresh onRefresh) {
        Request rq = obj.getRequestObject();
        IResponseInfo response = obj.getRequestObject().getAnalysedResponse();
        if(outPar.getType() == ResponseOut.TYPE_CAPTCHA){
            String captcha = showCaptcha(obj,onRefresh);
            updateVariable(instance,outPar.getName(),captcha,outPar.isGlobal());
            return;
        }
        if(outPar.getType() == ResponseOut.TYPE_COOKIE){
            for (ICookie coo :response.getCookies()) {
                String cookie = outPar.getParam();
                if (cookie == null || cookie.trim().isEmpty()) {
                    cookie = Settings.getCookie();
                }
                if (coo.getName().equals(cookie)) {
                    updateVariable(instance,outPar.getName(),coo.getValue(),outPar.isGlobal());
                    return;
                }
            }
            return;
        }
        if(outPar.getType() == ResponseOut.TYPE_REGEX){
            String allres = new String(rq.getResponse(), Charset.forName("UTF-8"));
            Pattern p = Pattern.compile(outPar.getParam());
            Matcher m = p.matcher(allres);
            if(m.find()) {
                updateVariable(instance,outPar.getName(),m.group(),outPar.isGlobal());
            }
            return;
        }

        byte[] bodyBytes = new byte[rq.getResponse().length - response.getBodyOffset()];
        System.arraycopy(rq.getResponse(),response.getBodyOffset(),bodyBytes,0,bodyBytes.length);
        String body = new String(bodyBytes, Charset.forName("UTF-8"));
        Document htmlDoc = Jsoup.parse(body);
        if(outPar.getType() == ResponseOut.TYPE_HIDDEN){
            Elements hidden = htmlDoc.getElementsByTag("input[name=\""+outPar.getParam().replaceAll("\"","\\\"")+"\"]");
            updateVariable(instance,outPar.getName(),hidden.val(),outPar.isGlobal());
            return;
        }
        if(outPar.getType() == ResponseOut.TYPE_CSS){
            Elements res = htmlDoc.select(outPar.getParam());
            // TODO get a second parameter for attribute of element, after css selector
            String val = res.val();
            if(val.isEmpty()){
                val = res.text();
            }
            if(val.isEmpty()){
                val = res.html();
            }
            updateVariable(instance,outPar.getName(),val,outPar.isGlobal());
            return;
        }
    }

    private static void updateVariable(Flow_Running instance,String vname, String vvalue,boolean isGlobal){
        if(isGlobal) {
            if(vname.equalsIgnoreCase("return")){
                instance.updateGlobalVariable(instance.getName(), vvalue);
            }else {
                instance.updateGlobalVariable(vname, vvalue);
            }
        } else {
            instance.updateLocalVariable(vname,vvalue);
        }

    }

    private static void updateVariable_parametric(Flow_Running instance,String vname, String vvalue,boolean isGlobal){
        String real_vname = vname;
        int MAAX = 100;
        Pattern pattern = Pattern.compile(Settings.TYPE_PATTERN);
        Matcher matcher = pattern.matcher(real_vname);
        int reference_time =0;
        while (matcher.find()){
            if(reference_time>MAAX){
                BurpExtender.logText("Loop Detected. vname=" + vname + ", real_vname="+real_vname);
                break;
            }
            reference_time +=1;
            String varname = matcher.group(1);
            String vartype = matcher.group(2);
            if(vartype.equals("params")){
                real_vname = instance.getParams().get(varname);
            }
            if(vartype.equals("locals")){
                real_vname = instance.getLocals().get(varname);
            }
            if(vartype.equals("globals")){
                real_vname = instance.getGLOBALS().get(varname);
            }
            matcher = pattern.matcher(real_vname);
        }

        if(isGlobal) {
            instance.updateGlobalVariable(real_vname,vvalue);
        } else {
            instance.updateLocalVariable(real_vname,vvalue);
        }

    }

    private static String showCaptcha(RequestListModelObject obj, Controller.OnCaptchaRefresh onRefresh) {
        DialogCaptcha dlg = new DialogCaptcha();
        return dlg.setData(obj,onRefresh);
    }

    public static void exportFlow(int id, File file) {
        exportFlow(new int[]{id},file);
    }

    public static void exportFlow(int[] ids, File file) {
        Flow[] flows = new Flow[ids.length];
        for (int i = 0; i < ids.length; i++) {
            flows[i] = Flow.getById(ids[i]);
        }
        exportFlow(flows,file);
    }

    private static void exportFlow(Flow[] flows, File file) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.excludeFieldsWithoutExposeAnnotation().registerTypeHierarchyAdapter(byte[].class,
                new ByteArrayToBase64TypeAdapter()).create();
        try {
            String json = gson.toJson(flows);
            fileWrite(file,json);

        } catch (Exception e) {
            e.printStackTrace(BurpExtender.getInstance().getStdout());
        }
    }

    private static void fileWrite(File file, String content) {
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static boolean isValidURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void exportJSON(File file) {
        List<Flow> flows = Flow.getAllFlows();
        Flow[] fl_arr = new Flow[flows.size()];
        for (int i = 0; i < fl_arr.length; i++) {
            fl_arr[i] = flows.get(i);
        }
        exportFlow(fl_arr,file);
    }

    public static void importJSON(File file) {
        // TODO importJSON
    }

    public static boolean isInFilter(URL url) {
        for (String filter : Settings.getFilters()) {
            if(url.getPath().endsWith(filter)){
                return true;
            }
        }
        return false;
    }


    private static class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
        @Override
        public byte[] deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return Base64.decode(json.getAsString(), Base64.NO_WRAP);
        }

        @Override
        public JsonElement serialize(byte[] src, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(Base64.encodeToString(src, Base64.NO_WRAP));
        }

    }

    public static String readAsset(String filename) throws IOException {
//        URL url = new URL("jar:file:/"+BurpExtender.getInstance().getCallbacks().getExtensionFilename()+"!/assets/"+ filename);
//        InputStream in = url.openStream();
//        @url: https://support.portswigger.net/customer/portal/questions/17136484-extension-resource-loading
        InputStream in = DataUtils.class.getClassLoader().getResourceAsStream("assets/" + filename);
        StringWriter writer = new StringWriter();
        IOUtils.copy(in, writer, StandardCharsets.UTF_8);
        in.close();
        return writer.toString();
    }
}
