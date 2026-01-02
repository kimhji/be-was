package webserver;

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
    SimpleReq(String req){
        if(req == null || req.isBlank()) throw new RuntimeException("요청이 비어있습니다.");
        String[] lines = req.split("\n");
        String[] firstHeader = lines[0].trim().split(" ");
        if(firstHeader.length < 3){
            throw new RuntimeException("헤더의 첫 줄이 예상하지 못한 형식으로 들어왔습니다.");
        }
        method = getMethod(firstHeader[0]);
        path = firstHeader[1];
        String[] pathSplit = firstHeader[1].split("\\?");
        if(pathSplit.length>1){
            for(String param: pathSplit[1].split("&")){
                String[] keyAndValue = param.split("=");
                if(keyAndValue.length<=1) continue;
                queryParam.put(keyAndValue[0],keyAndValue[1]);
            }
            path = pathSplit[0];
        }
    }
    SimpleReq(Method method, String path) {
        this.method = method;
        this.path = path;
    }

    private Method getMethod(String methodStr){
        if(methodStr==null || methodStr.isBlank()) throw new RuntimeException("알맞지 않은 method입니다.");
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
                throw new RuntimeException("알맞지 않은 method입니다.");
        }
    }
}