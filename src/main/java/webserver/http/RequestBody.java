package webserver.http;

import common.Config;
import common.Utils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {
    Map<String, String> header = new HashMap<>();
    byte[] content;

    public RequestBody(String content) {
        this.content = content.getBytes(StandardCharsets.UTF_8);
    }

    public RequestBody(byte[] data) {
        //TODO: parse data
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
