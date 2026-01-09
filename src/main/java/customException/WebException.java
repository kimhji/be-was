package customException;


public class WebException extends RuntimeException {
    public enum HTTPStatus {
        OK(200),
        CREATED(201),
        NO_CONTENT(204),
        MOVED_PERMANENTLY(301),
        MOVED_TEMPORALLY(302),
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        NOT_FOUND(404),
        METHOD_NOT_ALLOWED(405),
        CONFLICT(409),
        INTERNAL_SERVER_ERROR(500);

        private final int httpStatus;

        public int getHttpStatus() {
            return this.httpStatus;
        }

        HTTPStatus(int httpStatus) {
            this.httpStatus = httpStatus;
        }
    }

    public HTTPStatus statusCode;
    public String path;

    WebException(HTTPStatus statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.path = "";
    }

    WebException(HTTPStatus statusCode, String message, String path) {
        super(message);
        this.statusCode = statusCode;
        this.path = path;
    }
}
