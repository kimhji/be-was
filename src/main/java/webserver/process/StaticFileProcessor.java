package webserver.process;

import common.Config;
import common.Utils;
import customException.DBExceptionConverter;
import customException.WebStatusConverter;
import webserver.http.Request;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;

public class StaticFileProcessor {
    private static String basePath = "./src/main/resources/static";

    public static byte[] processReq(Request simpleReq) {
        byte[] result = "".getBytes();
        if (simpleReq != null && simpleReq.method == Request.Method.GET) {
            result = getStaticSources(simpleReq.path);
        }
        return result;
    }

    static public void checkImageType(byte[] data) {
        String type = Utils.detectImageType(data);
        if(type.compareTo(Config.IMAGE_TYPE_UNKNOWN) == 0) throw DBExceptionConverter.notAppliedImageType();
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