package webserver.route;

import webserver.http.Response;
import webserver.http.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RouterNode {
    Map<String, RouterNode> children = new HashMap<>();
    Map<Request.Method, Function<Request, Response>> funcs = new HashMap<>();
}
