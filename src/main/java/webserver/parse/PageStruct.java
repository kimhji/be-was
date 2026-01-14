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
        headerContent.setPage(true, PageReplacer.PageType.DEFAULT, "<div>" +
                    "<ul class=\"header__menu\">" +
                    "  <li class=\"header__menu__item\">" +
                    "    <img id=link_to_mypage class=\"post__account__img\" />" +
                    "  </li>" +
                    "  <li class=\"header__menu__item\">" +
                    "    <p id=link_to_mypage class=\"post__account__nickname\">안녕하세요, {{user.name}}님!</p>" +
                    "  </li>" +
                    "<li class=\"header__menu__item\">\n" +
                    "<a class=\"btn btn_contained btn_size_s\" href=\"/write\">글쓰기</a>" +
                    "          </li>\n" +
                    "          <li class=\"header__menu__item\">\n" +
                    "            <button id=\"logout-btn\" class=\"btn btn_ghost btn_size_s\">\n" +
                    "              로그아웃\n" +
                    "            </button>\n" +
                    "          </li>"+
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
        headerContent.setPage(true,PageReplacer.PageType.ARTICLE, "<div>" +
                "<ul class=\"header__menu\">" +
                "  <li class=\"header__menu__item\">" +
                "    <img id=link_to_mypage class=\"post__account__img\" />" +
                "  </li>" +
                "  <li class=\"header__menu__item\">" +
                "    <p id=link_to_mypage class=\"post__account__nickname\">안녕하세요, {{user.name}}님!</p>" +
                "  </li>" +
                "<li class=\"header__menu__item\">\n" +
                "<a class=\"btn btn_contained btn_size_s\" href=\"/write\">글쓰기</a>" +
                "          </li>\n" +
                "          <li class=\"header__menu__item\">\n" +
                "            <button id=\"logout-btn\" class=\"btn btn_ghost btn_size_s\">\n" +
                "              로그아웃\n" +
                "            </button>\n" +
                "          </li>"+
                "</ul>" +
                "</div>");

    }
}
