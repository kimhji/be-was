package webserver.route;

import common.Config;
import customException.WebStatusConverter;
import webserver.http.Response;
import webserver.http.Request;
import webserver.process.Processor;

import java.util.function.Function;

public class Router {

    private final RouterNode root = new RouterNode();

    public static boolean needLogin(String path){
        return (path.compareTo(Config.MY_PAGE_PAGE_PATH) == 0) || (path.compareTo("/mypage") == 0)
                || (path.compareTo("/write") ==0)|| (path.compareTo(Config.ARTICLE_PAGE_PATH)==0);
    }

    public static boolean needPostData(String path){
        return (path.compareTo(Config.DEFAULT_PAGE_PATH) == 0) || (path.compareTo("/") == 0)
                || (path.compareTo(Config.MAIN_PAGE_PATH)==0);
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
