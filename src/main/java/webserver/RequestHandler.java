package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import common.Config;
import customException.UserExceptionConverter;
import customException.WebException;
import customException.WebStatusConverter;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.parse.PageReplacer;
import webserver.parse.Replacer;
import webserver.process.Processor;
import webserver.process.StaticFileProcessor;
import webserver.process.UserProcessor;
import webserver.http.Response;
import webserver.http.Request;
import webserver.route.Router;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final Processor processor = new Processor();

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);
            try {
                Request simpleReq = getReq(in);
                logger.debug(simpleReq.toString());
                Response response = processor.process(simpleReq);

                response.setResponseHeader(dos);
                response.responseBody(dos);
            } catch (WebException e) {
                Response response = new Response(e.statusCode, e.getMessage().getBytes(), Response.ContentType.PLAIN_TEXT);
                if (e.path != null && !e.path.isBlank())
                    response.addHeader(Config.HEADER_LOCATION, e.path);
                response.setResponseHeader(dos);
                response.responseBody(dos);
            }
        } catch (IOException e) {
            logger.debug("Connection closed", e);
        } catch (Exception e) {
            logger.error("Unhandled error", e);
        }
    }

    private Request getReq(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8)
        );

        String line;
        StringBuilder headerPart = new StringBuilder();

        while ((line = br.readLine()) != null && !line.isEmpty()) {
            headerPart.append(line).append(Config.CRLF);
        }
        Request req = new Request(headerPart.toString());

        String contentLength = req.header.get(Config.HEADER_CONTENT_LENGTH);
        if (contentLength != null) {
            int len = Integer.parseInt(contentLength);

            if (len <= 0) return req;

            char[] bodyChars = new char[len];
            int read = 0;
            while (read < len) {
                read += br.read(bodyChars, read, len - read);
            }

            String body = new String(bodyChars);
            req.addBodyParam(body);
        }

        return req;
    }


}
