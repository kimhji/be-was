package common;

import webserver.http.RequestBody;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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

    public static List<byte[]> splitBytesExceptFirst(byte[] origin, byte[] splitter){
        List<byte[]> result = splitBytes(origin, splitter);
        if(result.size() <= 1) return new LinkedList<>();
        return result.subList(1, result.size());
    }
    public static List<byte[]> splitBytes(byte[] origin, byte[] splitter){
        int idx = -1;
        int count = 0;
        int firstIdx = 0;
        List<byte[]> result = new LinkedList<>() ;
        for(byte oneByte: origin){
            idx++;
            if(oneByte != splitter[count]){
                count = 0;
                continue;
            }
            count++;
            if(count == splitter.length){
                count = 0;
                byte[] part = Arrays.copyOfRange(origin, firstIdx, idx+1-splitter.length);
                result.add(part);
                firstIdx = idx+1;
            }
        }
        return result;
    }

    public static byte[] getFirstSplit (byte[] origin, byte[] splitter){
        int idx = -1;
        int count = 0;
        for(byte oneByte: origin){
            idx++;
            if(oneByte != splitter[count]){
                count = 0;
                continue;
            }
            count++;
            if(count == splitter.length){
                return Arrays.copyOfRange(origin, idx+1, origin.length);
            }
        }
        return null;
    }

    public static List<byte[]> getBothSplit (byte[] origin, byte[] splitter){
        int idx = -1;
        int count = 0;
        List<byte[]> result = new LinkedList<>();
        for(byte oneByte: origin){
            idx++;
            if(oneByte != splitter[count]){
                count = 0;
                continue;
            }
            count++;
            if(count == splitter.length){
                result.add(Arrays.copyOfRange(origin, 0, idx+1 - splitter.length));
                result.add(Arrays.copyOfRange(origin, idx+1, origin.length));
                return result;
            }
        }
        return null;
    }
}
