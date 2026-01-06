package webserver;

import customException.WebException;

public class Response {
    WebException.HTTPStatus statusCode;
    String contentType;
    byte[] body;

    public Response(WebException.HTTPStatus status,
                        byte[] body,
                        String contentType) {
        this.statusCode = status;
        this.body = body;
        this.contentType = contentType;
    }
}
