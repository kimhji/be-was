package common;

public class Config {
    public static final String CRLF = "\r\n";
    public static final String REPLACE_PLACEHOLDER = "page";

    public static final String DEFAULT_PAGE_PATH = "/index.html";
    public static final String MAIN_PAGE_PATH = "/main/index.html";
    public static final String REGISTRATION_PAGE_PATH = "/registration/index.html";
    public static final String LOGIN_PAGE_PATH = "/login/index.html";
    public static final String MY_PAGE_PAGE_PATH = "/mypage/index.html";
    public static final String COMMENT_PAGE_PATH = "/comment/index.html";
    public static final String ARTICLE_PAGE_PATH = "/article/index.html";
    public static final String NOPOST_PAGE_PATH = "/exception/nopost.html";
    public static final String COMMENT_PAGE_PATH_PREFIX = "/comment";
    public static final String POST_PAGE_PATH = "/post";

    public static final String HEADER_CONTENT_LENGTH = "content-length";
    public static final String HEADER_LOCATION = "location";
    public static final String HEADER_SET_COOKIE = "set-cookie";
    public static final String HEADER_COOKIE = "cookie";
    public static final String HEADER_CONTENT_TYPE = "content-type";
    public static final String HEADER_BOUNDARY = "boundary";

    public static final String IMAGE_BASE_PATH = "./src/main/resources/uploads";
    public static final String IMAGE_PROFILE_BASE_PATH = "./src/main/resources/uploads/profiles";
    public static final String IMAGE_DEFAULT_PROFILE_NAME = "default.png";

    public static final String POST_ID_QUERY_NAME = "postId";

    public static final String PAGE_HEADER_LOGIN = "<div>" +
            "<ul class=\"header__menu\">" +
            "  <li class=\"header__menu__item\">" +
            "    <img id=link_to_mypage class=\"post__account__img\" src=\"/image/profile/{{user.imagePath}}\" />" +
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
            "          </li>" +
            "</ul>" +
            "</div>";
    public static final String PAGE_HEADER_NOT_LOGIN = "<ul class=\"header__menu\">" +
            "  <li class=\"header__menu__item\">" +
            "    <a class=\"btn btn_contained btn_size_s\" href=\"/login\">로그인</a>" +
            "  </li>" +
            "  <li class=\"header__menu__item\">" +
            "    <a class=\"btn btn_ghost btn_size_s\" href=\"/registration\">회원 가입</a>" +
            "  </li>" +
            "</ul>";
}
