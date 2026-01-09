package webserver.http;

import common.Config;
import common.Utils;
import customException.WebStatusConverter;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Request {
    public enum Method {
        GET,
        POST,
        PUT,
        DELETE,
        PATCH
    }

    public Method method;
    public String path;
    public Map<String, String> queryParam = new HashMap<>();
    public Map<String, String> header = new HashMap<>();
    public Map<String, String> bodyParam = new HashMap<>();

    public Request(String req) {
        if (req == null || req.isBlank()) throw WebStatusConverter.emptyRequest();
        String[] lines = req.split("\n");
        getReqStartLine(lines[0]);
        boolean isFindEmptyLine = false;
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].trim().isBlank()) {
                isFindEmptyLine = true;
            }
            if (!isFindEmptyLine || method != Method.POST) {
                addHeader(lines[i]);
            } else {
                addBodyParam(lines[i]);
            }
        }
    }

    public Request(Method method, String path) {
        this.method = method;
        this.path = path;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(method.name())
                .append(" ")
                .append(buildPathWithQuery())
                .append(" HTTP/1.1")
                .append(Config.CRLF);

        for (Map.Entry<String, String> entry : header.entrySet()) {
            sb.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(Config.CRLF);
        }

        sb.append(Config.CRLF);

        if (!bodyParam.isEmpty()) {
            sb.append(Utils.parseMapToQueryString(bodyParam));
        }

        return sb.toString();
    }

    private String buildPathWithQuery() {
        if (queryParam.isEmpty()) return path;

        StringBuilder sb = new StringBuilder(path);
        sb.append("?");

        sb.append(Utils.parseMapToQueryString(queryParam));
        return sb.toString();
    }


    private void getReqStartLine(String startLine) {
        String[] firstHeader = startLine.trim().split(" ");
        if (firstHeader.length < 3) {
            throw WebStatusConverter.invalidFirstHeaderRequest();
        }
        method = getMethod(firstHeader[0]);
        path = firstHeader[1];
        String[] pathSplit = firstHeader[1].split("\\?");
        if (pathSplit.length > 1) {
            String paramStr = Utils.getRestStr(firstHeader[1], "\\?", 1).trim();
            for (String param : paramStr.split("&")) {
                String[] keyAndValue = param.split("=");
                if (keyAndValue.length <= 1) continue;
                queryParam.put(URLDecoder.decode(keyAndValue[0], UTF_8), URLDecoder.decode(keyAndValue[1], UTF_8));
            }
            path = pathSplit[0];
        }
    }

    public void addBodyParam(String line) {
        if (line == null || line.isBlank()) return;
        String[] cases = line.split("&");
        for (String keyValue : cases) {
            String[] split = keyValue.split("=");
            if (split.length < 2) continue;
            bodyParam.put(URLDecoder.decode(split[0].trim(), UTF_8), URLDecoder.decode(split[1].trim(), UTF_8));
        }
    }

    private void addHeader(String line) {
        if (line == null || line.isBlank()) return;
        String[] words = line.trim().split(":");
        if (words.length < 2) return;
        String value = Utils.getRestStr(line, ":", 1).trim();
        header.put(words[0].trim().toLowerCase(), URLDecoder.decode(value, UTF_8));
    }

    private Method getMethod(String methodStr) {
        if (methodStr == null || methodStr.isBlank()) throw WebStatusConverter.invalidMethod();
        switch (methodStr.trim().toUpperCase()) {
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
                throw WebStatusConverter.invalidMethod();
        }
    }
}