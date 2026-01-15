package customException;

public class PostExceptionConverter{
    public static WebException notFoundPost() {
        return new WebException(
                WebException.HTTPStatus.NOT_FOUND,
                "포스트를 찾을 수 없습니다."
        );
    }

    public static WebException badContentPost() {
        return new WebException(
                WebException.HTTPStatus.BAD_REQUEST,
                "포스트의 내용이 존재하지 않습니다."
        );
    }

    public static WebException badPostId() {
        return new WebException(
                WebException.HTTPStatus.BAD_REQUEST,
                "포스트 id가 형식에 맞지 않습니다."
        );
    }
}
