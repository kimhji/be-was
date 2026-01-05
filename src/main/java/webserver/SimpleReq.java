package webserver;

import common.UtilFunc;
import customException.WebStatusConverter;

import java.util.HashMap;
import java.util.Map;

class SimpleReq{
    enum Method{
        GET,
        POST,
        PUT,
        DELETE,
        PATCH
    }

    Method method;
    String path;
    Map<String, String> queryParam = new HashMap<>();
    Map<String, String> header = new HashMap<>();
    Map<String, String> bodyParam = new HashMap<>();
    SimpleReq(String req){
        if(req == null || req.isBlank()) throw WebStatusConverter.emptyRequest();
        String[] lines = req.split("\n");
        getReqStartLine(lines[0]);
        boolean isFindEmptyLine = false;
        for(int i = 1;i<lines.length;i++){
            if(lines[i].trim().isBlank()){
                isFindEmptyLine = true;
            }
            if(!isFindEmptyLine || method != Method.POST){
                addHeader(lines[i]);
            }
            else{

            }
        }
    }

    SimpleReq(Method method, String path) {
        this.method = method;
        this.path = path;
    }

    private void getReqStartLine(String startLine){
        String[] firstHeader = startLine.trim().split(" ");
        if(firstHeader.length < 3){
            throw WebStatusConverter.invalidFirstHeaderRequest();
        }
        method = getMethod(firstHeader[0]);
        path = firstHeader[1];
        String[] pathSplit = firstHeader[1].split("\\?");
        if(pathSplit.length>1){
            String paramStr = UtilFunc.getRestStr(firstHeader[1], "\\?", 1).trim();
            for(String param: paramStr.split("&")){
                String[] keyAndValue = param.split("=");
                if(keyAndValue.length<=1) continue;
                queryParam.put(keyAndValue[0],keyAndValue[1]);
            }
            path = pathSplit[0];
        }
    }

    private void addBodyParam(String line){

    }

    private void addHeader(String line){
        if(line == null || line.isBlank()) return;
        String[] words = line.trim().split(":");
        if(words.length<2) return;
        String value = UtilFunc.getRestStr(line, ":", 1).trim();
        header.put(words[0], value);
    }

    private Method getMethod(String methodStr){
        if(methodStr==null || methodStr.isBlank()) throw WebStatusConverter.invalidMethod();
        switch(methodStr.trim().toUpperCase()){
            case "GET":
                return Method.GET;
            case "POST":
                return Method.POST;
            case "PUT":
                return Method.PUT;
            case "DELETE":
                return Method.DELETE;
            case "PATCH":
                return Method.PATCH;
            default:
                throw WebStatusConverter.invalidMethod();
        }
    }
}