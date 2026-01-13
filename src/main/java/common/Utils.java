package common;

import webserver.http.RequestBody;

import java.util.Map;

public class Utils {
    public static String getRestStr(String wholeStr, String splitParam, int idx) {
        if (wholeStr == null || splitParam == null || wholeStr.isBlank() || splitParam.isBlank()) return "";
        String[] strs = wholeStr.split(splitParam);
        if (strs.length <= idx) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = idx; i < strs.length; i++) {
            sb.append(strs[i]);
            if (i + 1 < strs.length) {
                sb.append(splitParam);
            }
        }
        return sb.toString();
    }

    public static String parseMapToQueryString_RequestBody(Map<String, RequestBody> queryParam) {
        StringBuilder sb = new StringBuilder();

        boolean first = true;
        for (Map.Entry<String, RequestBody> entry : queryParam.entrySet()) {
            if (!first) sb.append("&");
            sb.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue().toString());
            first = false;
        }
        return sb.toString();
    }
    public static String parseMapToQueryString(Map<String, String> queryParam) {
        StringBuilder sb = new StringBuilder();

        boolean first = true;
        for (Map.Entry<String, String> entry : queryParam.entrySet()) {
            if (!first) sb.append("&");
            sb.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue());
            first = false;
        }
        return sb.toString();
    }
    public static void replaceAll(StringBuilder sb, String target, String replacement) {
        int index;
        if (replacement == null) replacement = "";
        while ((index = sb.indexOf(target)) != -1) {
            sb.replace(index, index + target.length(), replacement);
        }
    }
}
