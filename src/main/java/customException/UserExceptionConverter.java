package customException;

public class UserExceptionConverter {
    public static WebException conflictUserID() {
        return new WebException(
                WebException.HTTPStatus.CONFLICT,
                "해당 아이디를 사용하는 회원이 존재합니다."
        );
    }

    public static WebException conflictUserName() {
        return new WebException(
                WebException.HTTPStatus.CONFLICT,
                "해당 이름을 사용하는 회원이 존재합니다."
        );
    }

    public static WebException needUserId() {
        return new WebException(
                WebException.HTTPStatus.BAD_REQUEST,
                "사용자 아이디를 요청에 포함해야 합니다."
        );
    }

    public static WebException tooShortUserId() {
        return new WebException(
                WebException.HTTPStatus.BAD_REQUEST,
                "사용자 아이디는 4자 이상이어야 합니다."
        );
    }

    public static WebException tooShortUserName() {
        return new WebException(
                WebException.HTTPStatus.BAD_REQUEST,
                "사용자명은 4자 이상이어야 합니다."
        );
    }

    public static WebException tooShortUserPassword() {
        return new WebException(
                WebException.HTTPStatus.BAD_REQUEST,
                "비밀번호는 4자 이상이어야 합니다."
        );
    }

    public static WebException needUserName() {
        return new WebException(
                WebException.HTTPStatus.BAD_REQUEST,
                "사용자 이름을 요청에 포함해야 합니다."
        );
    }

    public static WebException needUserData() {
        return new WebException(
                WebException.HTTPStatus.BAD_REQUEST,
                "사용자 데이터가 충분히 요청에 포함되지 않았습니다."
        );
    }

    public static WebException notFoundUser() {
        return new WebException(
                WebException.HTTPStatus.NOT_FOUND,
                "사용자 데이터가 존재하지 않습니다."
        );
    }

    public static WebException unAuthorized() {
        return new WebException(
                WebException.HTTPStatus.UNAUTHORIZED,
                "비밀번호가 틀렸습니다."
        );
    }

    public static WebException needToLogin() {
        return new WebException(
                WebException.HTTPStatus.MOVED_TEMPORALLY,
                "로그인이 필요합니다.",
                "/login"
        );
    }


    public static WebException passwordNotMatch() {
        return new WebException(
                WebException.HTTPStatus.BAD_REQUEST,
                "비밀번호가 일치하지 않습니다."
        );
    }

    public static WebException failedUserUpdate() {
        return new WebException(
                WebException.HTTPStatus.INTERNAL_SERVER_ERROR,
                "DB 내 사용자 데이터를 업데이트하는 것을 실패했습니다."
        );
    }
}
