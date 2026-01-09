package webserver.route;

import customException.WebStatusConverter;
import webserver.http.Response;
import webserver.http.Request;

import java.util.function.Function;

public class Router {

    private final RouterNode root = new RouterNode();

    public static boolean needLogin(String path){
        return (path.compareTo("/mypage/index.html") == 0) || (path.compareTo("/mypage") == 0);
    }

    public void register(Request req, Function<Request, Response> func) {
        String[] parts = req.path.split("/");
        RouterNode curNode = root;

        for (String part : parts) {
            if (part.isEmpty()) continue;
            curNode = curNode.children
                    .computeIfAbsent(part, k -> new RouterNode());
        }
        curNode.funcs
                .put(req.method, func);
    }

    public Response route(Request req) {
        String[] parts = req.path.split("/");
        RouterNode curNode = root;

        for (String part : parts) {
            if (part.isEmpty()) continue;
            curNode = curNode.children.get(part);
            if (curNode == null) throw WebStatusConverter.notAllowedPath();
        }

        Function<Request, Response> func = curNode.funcs.get(req.method);
        if (func == null)
            throw WebStatusConverter.notAllowedMethod();
        return func.apply(req);
    }
}
