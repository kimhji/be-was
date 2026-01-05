package customException;


public class WebException extends RuntimeException{
    public enum HTTPStatus {
        OK(200),
        CREATED(201),
        NO_CONTENT(204),
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        NOT_FOUND(404),
        INTERNAL_SERVER_ERROR(500);

        private final int httpStatus;

        public int getHttpStatus(){
            return this.httpStatus;
        }

        HTTPStatus(int httpStatus) {
            this.httpStatus = httpStatus;
        }
    }

    HTTPStatus statusCode;

    WebException(HTTPStatus statusCode, String message){
        super(message);
        this.statusCode = statusCode;
    }
}
