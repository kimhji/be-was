package webserver.http;

import common.Config;
import common.Utils;
import customException.WebStatusConverter;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestBody {
    Map<String, String> header = new HashMap<>();
    byte[] content;

    public RequestBody(String content) {
        this.content = content.getBytes(StandardCharsets.UTF_8);
    }

    public RequestBody(byte[] data) {
        if(data == null) throw WebStatusConverter.emptyRequest();
        List<byte[]> headerAndBody = Utils.getBothSplit(data, (Config.CRLF+Config.CRLF).getBytes(StandardCharsets.UTF_8));
        if(headerAndBody == null || headerAndBody.size() <= 1) throw WebStatusConverter.emptyRequest();
        List<byte[]> bodyParsing = Utils.getBothSplit(data, (Config.CRLF+Config.CRLF).getBytes(StandardCharsets.UTF_8));
        content = headerAndBody.get(1);
        for(byte[] line: Utils.splitBytes(headerAndBody.get(0), Config.CRLF.getBytes(StandardCharsets.UTF_8))){
            List<byte[]> keyAndValue = Utils.getBothSplit(line, "=".getBytes(StandardCharsets.UTF_8));
            if(keyAndValue == null || keyAndValue.size() <= 1) continue;
            header.put(new String (keyAndValue.get(0)), new String(keyAndValue.get(1)));
        }
    }

    public byte[] getContent() {
        return content;
    }

    public String getContentString(){
        return new String(content);
    }

    @Override
    public String toString() {
        if (header.isEmpty()) return new String(content);

        StringBuilder builder = new StringBuilder();
        builder.append(Utils.parseMapToQueryString(header));
        builder.append(Config.CRLF + Config.CRLF);
        builder.append(new String(content));
        builder.append(Config.CRLF);
        return builder.toString();
    }
}
