package webserver.route;

import webserver.Response;
import webserver.SimpleReq;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RouterNode {
    Map<String, RouterNode> children = new HashMap<>();
    Map<SimpleReq.Method, Function<SimpleReq, Response>> funcs = new HashMap<>();
}
