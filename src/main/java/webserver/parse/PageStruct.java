package webserver.parse;

public class PageStruct {
    String header;
    String post;
    String comment;

    PageReplacer headerContent;
    PageReplacer postContent;
    PageReplacer commentContent;
    public PageStruct(){
        headerContent = new PageReplacer();
        postContent = new PageReplacer();
        commentContent = new PageReplacer();
        init();
    }

    public void setState(String path, boolean isLogin){
        header = headerContent.getParsedPage(path, isLogin);
        post = postContent.getParsedPage(path, isLogin);
        comment = commentContent.getParsedPage(path, isLogin);
    }

    private void init(){
        headerContent.setPage(false, PageReplacer.PageType.DEFAULT, "<ul class=\"header__menu\">" +
                    "  <li class=\"header__menu__item\">" +
                    "    <a class=\"btn btn_contained btn_size_s\" href=\"/login\">로그인</a>" +
                    "  </li>" +
                    "  <li class=\"header__menu__item\">" +
                    "    <a class=\"btn btn_ghost btn_size_s\" href=\"/registration\">회원 가입</a>" +
                    "  </li>" +
                    "</ul>");
        headerContent.setPage(true, PageReplacer.PageType.DEFAULT, "<div id=link_to_mypage>" +
                    "<ul class=\"header__menu\">" +
                    "  <li class=\"header__menu__item\">" +
                    "    <img class=\"post__account__img\" />" +
                    "  </li>" +
                    "  <li class=\"header__menu__item\">" +
                    "    <p class=\"post__account__nickname\">{{user.name}}</p>" +
                    "  </li>" +
                    "</ul>" +
                    "</div>");
        headerContent.setPage(false, PageReplacer.PageType.ARTICLE, "<ul class=\"header__menu\">" +
                    "  <li class=\"header__menu__item\">" +
                    "    <a class=\"btn btn_contained btn_size_s\" href=\"/login\">로그인</a>" +
                    "  </li>" +
                    "  <li class=\"header__menu__item\">" +
                    "    <a class=\"btn btn_ghost btn_size_s\" href=\"/registration\">회원 가입</a>" +
                    "  </li>" +
                    "</ul>");
        headerContent.setPage(true,PageReplacer.PageType.ARTICLE, "<div id=link_to_mypage>" +
                    "<ul class=\"header__menu\">" +
                    "  <li class=\"header__menu__item\">" +
                    "    <img class=\"post__account__img\" />" +
                    "  </li>" +
                    "  <li class=\"header__menu__item\">" +
                    "    <p class=\"post__account__nickname\">{{user.name}}</p>" +
                    "  </li>" +
                    "<button id=\"logout-btn\" class=\"btn btn_ghost btn_size_s\">로그아웃 </button>" +
                    "</ul>" +
                    "</div>");

    }
}
