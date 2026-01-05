package customException;

public class WebStatusConverter {
    public static WebException inexistenceStaticFile() {
        return new WebException(
                WebException.HTTPStatus.NOT_FOUND,
                "해당 static 파일이 존재하지 않습니다."
        );
    }
}
