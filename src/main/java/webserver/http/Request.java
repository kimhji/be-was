package webserver.http;

import common.Config;
import common.Utils;
import customException.WebStatusConverter;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

     public enum ContentType {
        MULTIPART_FORM_DATA("multipart/form-data"),
        APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded");

        public final String typeStr;

        ContentType(String typeStr) {
            this.typeStr = typeStr;
        }
    }

    public Method method;
    public String path;
    public Map<String, String> queryParam = new HashMap<>();
    public Map<String, String> header = new HashMap<>();
    public Map<String, RequestBody> bodyParam = new HashMap<>();

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
                addBodyParam(lines[i].getBytes(UTF_8));
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
            sb.append(Utils.parseMapToQueryString_RequestBody(bodyParam));
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

    public void addBodyParam(byte[] body) {
        switch (getContentType()) {
            case APPLICATION_X_WWW_FORM_URLENCODED:
                parseBodyByURLEncoded(body);
                break;

            case MULTIPART_FORM_DATA:
            default:
            // TODO: boundary 기반 파싱
                break;
        }
    }
    private ContentType getContentType() {
        String ct = header.get(Config.HEADER_CONTENT_TYPE);
        if (ct == null) return ContentType.APPLICATION_X_WWW_FORM_URLENCODED;

        if (ct.startsWith(ContentType.MULTIPART_FORM_DATA.typeStr)) {
            return ContentType.MULTIPART_FORM_DATA;
        }
        return ContentType.APPLICATION_X_WWW_FORM_URLENCODED;
    }

    private void parseBodyByURLEncoded(byte[] body) {
        if (body == null) return;
        String bodyStr = new String(body);
        if (bodyStr.isBlank()) return;
        String[] cases = bodyStr.split("&");
        for (String keyValue : cases) {
            String[] split = keyValue.split("=");
            if (split.length < 2) continue;
            bodyParam.put(URLDecoder.decode(split[0].trim(), UTF_8), new RequestBody(URLDecoder.decode(split[1].trim(), UTF_8)));
        }
    }

    private void parseBodyByMultipartForm(byte[] body){
        if (body == null) return;
        String[] split = header.getOrDefault(Config.HEADER_CONTENT_TYPE, "").split(Config.HEADER_BOUNDARY);
        if(split.length < 2) return;
        header.put(Config.HEADER_BOUNDARY.toLowerCase(), split[1].trim());

        byte[] splitter = split[1].trim().getBytes();
//        for (String keyValue : cases) {
//            String[] split = keyValue.split("=");
//            if (split.length < 2) continue;
//            bodyParam.put(URLDecoder.decode(split[0].trim(), UTF_8), new RequestBody(URLDecoder.decode(split[1].trim(), UTF_8)));
//        }
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
        return switch (methodStr.trim().toUpperCase()) {
            case "GET" -> Method.GET;
            case "POST" -> Method.POST;
            case "PUT" -> Method.PUT;
            case "DELETE" -> Method.DELETE;
            case "PATCH" -> Method.PATCH;
            default -> throw WebStatusConverter.invalidMethod();
        };
    }
}