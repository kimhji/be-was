package webserver;

public class Router {

    private final RouterNode root = new RouterNode();

    public void register(SimpleReq req, Runnable func) {
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

    public void route(SimpleReq req) {
        String[] parts = req.path.split("/");
        RouterNode curNode = root;

        for (String part : parts) {
            if (part.isEmpty()) continue;
            curNode = curNode.children.get(part);
            if (curNode == null) return;
        }

        Runnable func = curNode.funcs.get(req.method);
        if (func != null) {
            func.run();
        }
    }
}
