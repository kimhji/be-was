package customException;

public class WebStatusConverter {
    public static WebException inexistenceStaticFile() {
        return new WebException(
                WebException.HTTPStatus.NOT_FOUND,
                "해당 static 파일이 존재하지 않습니다."
        );
    }

    public static WebException fileReadError(){
        return new WebException(
                WebException.HTTPStatus.INTERNAL_SERVER_ERROR,
                "파일을 읽는 과정 중에 예상치 못한 에러가 발생했습니다."
        );
    }

    public static WebException emptyRequest(){
        return new WebException(
                WebException.HTTPStatus.BAD_REQUEST,
                "요청이 비어있습니다."
        );
    }

    public static WebException invalidFirstHeaderRequest(){
        return new WebException(
                WebException.HTTPStatus.BAD_REQUEST,
                "헤더의 첫 줄이 예상하지 못한 형식으로 들어왔습니다."
        );
    }

    public static WebException invalidMethod(){
        return new WebException(
                WebException.HTTPStatus.BAD_REQUEST,
                "알맞지 않은 method입니다."
        );
    }

    public static WebException notAllowedMethod(){
        return new WebException(
                WebException.HTTPStatus.METHOD_NOT_ALLOWED,
                "해당 method는 기능이 등록되어있지 않습니다."
        );
    }
}
