package customException;

public class UserExceptionConverter {
    public static WebException conflictUser() {
        return new WebException(
                WebException.HTTPStatus.CONFLICT,
                "해당 아이디를 사용하는 회원이 존재합니다."
        );
    }
}
