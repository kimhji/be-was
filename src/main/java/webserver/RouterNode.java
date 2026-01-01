package webserver;

import java.util.HashMap;
import java.util.Map;

public class RouterNode {
    Map<String, RouterNode> children = new HashMap<>();
    Map<SimpleReq.Method, Runnable> funcs = new HashMap<>();
}
