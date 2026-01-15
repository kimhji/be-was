package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import common.Config;
import customException.WebException;
import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.process.Processor;
import webserver.http.Response;
import webserver.http.Request;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final Processor processor = new Processor();

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public static void addTestUser(){
        Database.addUser(new User("tttt", "1234", "백엔드 현지"));
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
        ByteArrayOutputStream headerBuf = new ByteArrayOutputStream();

        int prev = -1, curr;
        while ((curr = in.read()) != -1) {
            headerBuf.write(curr);
            if (prev == '\r' && curr == '\n') {
                byte[] h = headerBuf.toByteArray();
                int len = h.length;
                if (len >= 4 &&
                        h[len-4] == '\r' && h[len-3] == '\n' &&
                        h[len-2] == '\r' && h[len-1] == '\n') {
                    break;
                }
            }
            prev = curr;
        }

        Request req = new Request(headerBuf.toString(StandardCharsets.UTF_8));
        String contentLength = req.header.get(Config.HEADER_CONTENT_LENGTH);
        if (contentLength != null) {
            int length = Integer.parseInt(contentLength);

            if (length <= 0) return req;

            byte[] body = new byte[length];
            int read = 0;
            while (read < length) {
                int tnsRead = in.read(body, read, length - read);
                if (tnsRead == -1) break;
                read += tnsRead;
            }
            req.addBodyParam(body);
        }

        return req;
    }
}
