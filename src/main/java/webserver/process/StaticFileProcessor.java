package webserver.process;

import common.UtilFunc;
import customException.WebStatusConverter;
import model.User;
import webserver.Request;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

public class StaticFileProcessor {
    private static String basePath = "./src/main/resources/static";

    public static byte[] processReq(Request simpleReq) {
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

    static public byte[] addUserData(byte[] staticFile, User user) {
        String html = new String(staticFile, StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder(html);

        String userDataHtml;

        if (user == null) {
            userDataHtml =
                    "<ul class=\"header__menu\">" +
                            "  <li class=\"header__menu__item\">" +
                            "    <a class=\"btn btn_contained btn_size_s\" href=\"/login\">로그인</a>" +
                            "  </li>" +
                            "  <li class=\"header__menu__item\">" +
                            "    <a class=\"btn btn_ghost btn_size_s\" href=\"/registration\">회원 가입</a>" +
                            "  </li>" +
                            "</ul>";
        } else {
            userDataHtml =
                    "<a href=\"/mypage\">"+
                    "<ul class=\"header__menu\">" +
                            "  <li class=\"header__menu__item\">" +
                            "    <img class=\"post__account__img\" />" +
                            "  </li>" +
                            "  <li class=\"header__menu__item\">" +
                            "    <p class=\"post__account__nickname\">" + user.getName() + "</p>" +
                            "  </li>" +
                            "</ul>"+
                            "</a>";
        }
        int idx = sb.indexOf("{{userData}}");
        if (idx != -1) {
            sb.replace(
                    idx,
                    idx + "{{userData}}".length(),
                    userDataHtml
            );
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}