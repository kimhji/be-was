package customException;

public class DBExceptionConverter {
    public static WebException failToSetDB() {
        return new WebException(
                WebException.HTTPStatus.INTERNAL_SERVER_ERROR,
                "DB 초기화 과정에서 에러가 발생했습니다."
        );
    }

    public static WebException failToAddUser() {
        return new WebException(
                WebException.HTTPStatus.BAD_REQUEST,
                "사용자 데이터를 DB에 저장하는 과정에서 에러가 발생했습니다."
        );
    }

    public static WebException failToFindUser() {
        return new WebException(
                WebException.HTTPStatus.BAD_REQUEST,
                "사용자 데이터를 DB에서 탐색하는 과정에서 에러가 발생했습니다."
        );
    }

    public static WebException failToUpdatePost(){
        return new WebException(
                WebException.HTTPStatus.INTERNAL_SERVER_ERROR,
                "포스트의 좋아요를 업데이트 하는 것에 실패했습니다."
        );
    }


    public static WebException failToAddPost() {
        return new WebException(
                WebException.HTTPStatus.BAD_REQUEST,
                "포스트 데이터를 DB에 저장하는 과정에서 에러가 발생했습니다."
        );
    }

    public static WebException failToFindPost() {
        return new WebException(
                WebException.HTTPStatus.BAD_REQUEST,
                "포스트 데이터를 DB에서 탐색하는 과정에서 에러가 발생했습니다."
        );
    }

    public static WebException failToLoadImage() {
        return new WebException(
                WebException.HTTPStatus.NOT_FOUND,
                "포스트의 이미지 데이터를 불러올 수 없습니다."
        );
    }

    public static WebException failToAddComment() {
        return new WebException(
                WebException.HTTPStatus.BAD_REQUEST,
                "댓글 데이터를 DB에 저장하는 과정에서 에러가 발생했습니다."
        );
    }

    public static WebException failToFindComment() {
        return new WebException(
                WebException.HTTPStatus.BAD_REQUEST,
                "댓글 데이터를 DB에서 탐색하는 과정에서 에러가 발생했습니다."
        );
    }
}
