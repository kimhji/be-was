package webserver;

class SimpleReq{
    enum Method{
        NONE,
        GET,
        POST,
        PUT,
        DELETE,
        PATCH
    }

    Method method;
    String path;
    SimpleReq(String req) throws Exception{
        if(req == null || req.isBlank()) throw new Exception("요청이 비어있습니다.");
        String[] lines = req.split("\n");
        String[] firstHeader = lines[0].trim().split(" ");
        if(firstHeader.length < 3){
            throw new Exception("헤더의 첫 줄이 예상하지 못한 형식으로 들어왔습니다.");
        }
        method = getMethod(firstHeader[0]);
        path = firstHeader[1];

        if(method == Method.NONE) throw new Exception("알맞지 않은 method입니다.");
    }
    SimpleReq(Method method, String path) {
        this.method = method;
        this.path = path;
    }

    private Method getMethod(String methodStr){
        if(methodStr==null || methodStr.isBlank()) return Method.NONE;
        switch(methodStr.trim()){
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
                return Method.NONE;
        }
    }
}