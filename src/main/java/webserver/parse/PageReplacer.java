package webserver.parse;

import common.Config;
import common.Utils;

import java.util.HashMap;
import java.util.Map;

public class PageReplacer {
    public enum PageType {
        DEFAULT,
        LOGIN,
        REGISTRATION,
        MAIN,
        MY_PAGE,
        COMMENT,
        ARTICLE
    }

    Map<PageType, String> notLogin = new HashMap<>();
    Map<PageType, String> afterLogin = new HashMap<>();

//    public String getWholePage(String template, String path, boolean isLogin) {
//        StringBuilder sb = new StringBuilder(template);
//
//        String placeholder = "{{" + Config.REPLACE_PLACEHOLDER + "}}";
//        Utils.replaceAll(sb, placeholder, getParsedPage(path, isLogin));
//        return sb.toString();
//    }

    public String getParsedPage(String path, boolean isLogin) {
        PageType pageType = getPageType(path);
        return (pageType != null && isLogin) ? afterLogin.get(pageType) : notLogin.get(pageType);
    }

    private PageType getPageType(String path) {
        if (path == null) return null;
        if (path.compareTo(Config.MY_PAGE_PAGE_PATH) == 0) return PageType.MY_PAGE;
        if (path.compareTo(Config.COMMENT_PAGE_PATH) == 0) return PageType.COMMENT;
        if (path.compareTo(Config.ARTICLE_PAGE_PATH) == 0) return PageType.ARTICLE;
        if (path.compareTo(Config.DEFAULT_PAGE_PATH) == 0) return PageType.DEFAULT;
        if (path.compareTo(Config.LOGIN_PAGE_PATH) == 0) return PageType.LOGIN;
        if (path.compareTo(Config.REGISTRATION_PAGE_PATH) == 0) return PageType.REGISTRATION;
        if (path.compareTo(Config.MAIN_PAGE_PATH) == 0) return PageType.MAIN;
        return null;
    }

    public void setPage(boolean needLogin, PageType type, String pagePart){
        if(needLogin){
            afterLogin.put(type, pagePart);
        }
        else{
            notLogin.put(type, pagePart);
        }
    }
}
