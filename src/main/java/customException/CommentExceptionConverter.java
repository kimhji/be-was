package customException;

import common.Config;

public class CommentExceptionConverter {
    public static WebException noPostId() {
        return new WebException(
                WebException.HTTPStatus.MOVED_TEMPORALLY,
                "댓글 작성 페이지에 대한 잘못된 접근입니다.",
                Config.DEFAULT_PAGE_PATH
        );
    }

    public static WebException badContentComment(){
        return new WebException(
                WebException.HTTPStatus.BAD_REQUEST,
                "댓글 내용이 비어있습니다."
        );
    }
}
