package webserver.route;

import customException.WebStatusConverter;
import webserver.Response;
import webserver.SimpleReq;

import java.util.function.Function;

public class Router {

    private final RouterNode root = new RouterNode();

    public void register(SimpleReq req, Function<SimpleReq, Response> func) {
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

    public Response route(SimpleReq req) {
        String[] parts = req.path.split("/");
        RouterNode curNode = root;

        for (String part : parts) {
            if (part.isEmpty()) continue;
            curNode = curNode.children.get(part);
            if (curNode == null) throw WebStatusConverter.notAllowedPath();
        }

        Function<SimpleReq, Response> func = curNode.funcs.get(req.method);
        if (func == null)
            throw WebStatusConverter.notAllowedMethod();
        return func.apply(req);
    }
}
