package webserver;

import customException.WebStatusConverter;

import java.util.function.Function;

public class Router {

    private final RouterNode root = new RouterNode();

    public void register(SimpleReq req, Function<SimpleReq,byte[]> func) {
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

    public byte[] route(SimpleReq req) {
        String[] parts = req.path.split("/");
        RouterNode curNode = root;

        for (String part : parts) {
            if (part.isEmpty()) continue;
            curNode = curNode.children.get(part);
            if (curNode == null) return null;
        }

        Function<SimpleReq, byte[]> func = curNode.funcs.get(req.method);
        if (func == null) {
            if (req.method == SimpleReq.Method.GET) return null;
            throw WebStatusConverter.notAllowedMethod();
        }
        return func.apply(req);
    }
}
