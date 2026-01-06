package webserver;

import customException.WebException;

import java.io.DataOutputStream;
import java.io.IOException;

public class Response {
    enum ContentType{
        HTML("text/html;charset=utf-8"),
        CSS("text/css"),
        JS("application/javascript"),
        PNG("image/png"),
        JPG("image/png"),
        JPEG("image/jpeg"),
        SVG("image/svg+xml"),
        OCTET("application/octet-stream"),
        PLAIN_TEXT("text/plain; charset=utf-8")
        ;

        public final String contentType;

        ContentType(String contentType){
            this.contentType = contentType;
        }
    }
    WebException.HTTPStatus statusCode;
    ContentType contentType;
    byte[] body;

    public Response(WebException.HTTPStatus status,
                        byte[] body,
                    ContentType contentType) {
        this.statusCode = status;
        this.body = body;
        this.contentType = contentType;
    }



    public void response200Header(DataOutputStream dos, int lengthOfBodyContent) throws IOException{
        responseHeaderByStatusAndType(dos, lengthOfBodyContent, WebException.HTTPStatus.OK, ContentType.HTML);
    }

    public void response200HeaderByType(DataOutputStream dos, int lengthOfBodyContent, SimpleReq simpleReq) throws IOException{
        responseHeaderByStatusAndType(dos, lengthOfBodyContent, WebException.HTTPStatus.OK, contentType(simpleReq.path));
    }

    public void responseHeaderByStatusAndType(DataOutputStream dos, int lengthOfBodyContent, WebException.HTTPStatus status, ContentType contentType) throws IOException{
        dos.writeBytes("HTTP/1.1 " + status.getHttpStatus() + " " + status.name() + " \r\n");
        dos.writeBytes("Content-Type: " + contentType.contentType + "\r\n");
        dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
    }

    public void setResponseHeader(DataOutputStream dos) throws IOException{
        dos.writeBytes("HTTP/1.1 " + statusCode.getHttpStatus() + " " + statusCode.name() + " \r\n");
        dos.writeBytes("Content-Type: " + contentType.contentType + "\r\n");
        dos.writeBytes("Content-Length: " + (body!=null?body.length:0) + "\r\n");
    }

    public static ContentType contentType(String path) {
        if (path.endsWith(".html")) return ContentType.HTML;
        if (path.endsWith(".css")) return ContentType.CSS;
        if (path.endsWith(".js")) return ContentType.JS;
        if (path.endsWith(".png")) return ContentType.PNG;
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return ContentType.JPEG;
        if (path.endsWith(".svg")) return ContentType.SVG;
        if (path.endsWith(".txt")) return ContentType.PLAIN_TEXT;
        return ContentType.OCTET;
    }
}
