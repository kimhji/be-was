package common;

public class Config {
    public static final String CRLF = "\r\n";
    public static final String REPLACE_PLACEHOLDER = "page";

    public static final int MIN_USER_DATA_LENGTH = 4;

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
    public static final String IMAGE_DEFAULT_PROFILE_API = "/image/profile/default.png";

    public static final String POST_ID_QUERY_NAME = "postId";

    public static final String REPEAT_FORMAT_PLACEHOLDER = "{{REPEAT}}";

    public static final String NO_COMMENT = "댓글이 없습니다. 먼저 작성을 시작해주세요!";

    public static final String IMAGE_TYPE_UNKNOWN = "unknown";
    public static final String IMAGE_TYPE_PNG = "png";
    public static final String IMAGE_TYPE_JPEG = "JPEG";


    public static final String PAGE_HEADER_LOGIN = "<div>" +
            "<ul class=\"header__menu\">" +
            "  <li class=\"header__menu__item\">" +
            "    <img class=\"post__account__img link_to_mypage\" src=\"/image/profile/{{user.imagePath}}\" />" +
            "  </li>" +
            "  <li class=\"header__menu__item\">" +
            "    <p class=\"post__account__nickname link_to_mypage\">안녕하세요, {{user.name}}님!</p>" +
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

    public static final String COMMENT_REPEAT_FORMAT = "<li class=\"comment__item\">\n" +
            "                <div class=\"comment__item__user\">\n" +
            "                    <img class=\"comment__item__user__img\" src=\"/image/profile/{{{{REPEAT}}.authorImagePath}}\"/>\n" +
            "                    <p class=\"comment__item__user__nickname\">{{{{REPEAT}}.authorName}}</p>\n" +
            "                </div>\n" +
            "                <p class=\"comment__item__article\">\n" +
            "                    {{{{REPEAT}}.content}}\n" +
            "                </p>\n" +
            "            </li>";
}
