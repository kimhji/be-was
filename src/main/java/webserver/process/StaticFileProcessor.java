package webserver.process;

import customException.WebStatusConverter;
import webserver.SimpleReq;

import java.io.File;
import java.io.FileInputStream;

public class StaticFileProcessor {
    private static String basePath = "./src/main/resources/static";

    public static byte[] processReq(SimpleReq simpleReq) {
        byte[] result = "".getBytes();
        switch (simpleReq.method) {
            case GET:
                result = getStaticSources(simpleReq.path);
                break;
            default:
                break;
        }
        return result;
    }

    static public void setBasePath(String basePath) {
        StaticFileProcessor.basePath = basePath;
    }

    static private byte[] getStaticSources(String path) {
        String wholePath = basePath + path;
        File file = new File(wholePath);
        if (!file.exists()) return null;
        if (!file.isFile()) return null;
        try (FileInputStream fr = new FileInputStream(wholePath)) {
            return fr.readAllBytes();
        } catch (Exception e) {
            throw WebStatusConverter.fileReadError();
        }
    }
}
