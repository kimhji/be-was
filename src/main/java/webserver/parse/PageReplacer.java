package webserver.parse;

import common.Config;
import common.UtilFunc;

import java.util.HashMap;
import java.util.Map;

public class PageReplacer {
    public enum PageType{
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
    public PageReplacer(){
        init();
    }

    public String getWholePage(String template, String path, boolean isLogin){
        StringBuilder sb = new StringBuilder(template);

        String placeholder = "{{" + Config.REPLACE_PLACEHOLDER + "}}";
        UtilFunc.replaceAll(sb, placeholder, getParsedPage(path, isLogin));
        return sb.toString();
    }

    private String getParsedPage(String path, boolean isLogin){
        PageType pageType = getPageType(path);
        return (pageType!=null&isLogin)?afterLogin.get(pageType):notLogin.get(pageType);
    }

    private PageType getPageType(String path){
        if(path == null) return null;
        if(path.compareTo(Config.MY_PAGE_PAGE_PATH) == 0) return PageType.MY_PAGE;
        if(path.compareTo(Config.COMMENT_PAGE_PATH) == 0) return PageType.COMMENT;
        if(path.compareTo(Config.ARTICLE_PAGE_PATH) == 0) return PageType.ARTICLE;
        if(path.compareTo(Config.DEFAULT_PAGE_PATH) == 0) return PageType.DEFAULT;
        if(path.compareTo(Config.LOGIN_PAGE_PATH) == 0) return PageType.LOGIN;
        if(path.compareTo(Config.REGISTRATION_PAGE_PATH) == 0) return PageType.REGISTRATION;
        if(path.compareTo(Config.MAIN_PAGE_PATH) == 0) return PageType.MAIN;
        return null;
    }

    private void init(){
        notLogin.put(PageType.DEFAULT,"<ul class=\"header__menu\">" +
                "  <li class=\"header__menu__item\">" +
                "    <a class=\"btn btn_contained btn_size_s\" href=\"/login\">로그인</a>" +
                "  </li>" +
                "  <li class=\"header__menu__item\">" +
                "    <a class=\"btn btn_ghost btn_size_s\" href=\"/registration\">회원 가입</a>" +
                "  </li>" +
                "</ul>");
        afterLogin.put(PageType.DEFAULT, "<div id=link_to_mypage>"+
                "<ul class=\"header__menu\">" +
                "  <li class=\"header__menu__item\">" +
                "    <img class=\"post__account__img\" />" +
                "  </li>" +
                "  <li class=\"header__menu__item\">" +
                "    <p class=\"post__account__nickname\">{{user.name}}</p>" +
                "  </li>" +
                "</ul>"+
                "</div>");
    }
}
