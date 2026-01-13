package webserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.http.Request;

import static org.assertj.core.api.Assertions.*;

class RequestTest {
//    @Test
//    void shouldFail() {
//        assertThat(1).isEqualTo(2);
//    }

    @Test
    @DisplayName("GET 요청 문자열을 파싱한다 (query parameter 포함)")
    void parseGetRequestWithQueryParam() {
        // given
        String req =
                "GET /test/path?name=kim&age=20 HTTP/1.1\n" +
                        "Host: localhost:8080\n";

        // when
        Request simpleReq = new Request(req);

        // then
        assertThat(simpleReq.method).isEqualTo(Request.Method.GET);
        assertThat(simpleReq.path).isEqualTo("/test/path");
        assertThat(simpleReq.queryParam)
                .containsEntry("name", "kim")
                .containsEntry("age", "20");
    }
    @Test
    @DisplayName("query parameter가 없는 요청을 파싱한다")
    void parseRequestWithoutQueryParam() {
        // given
        String req =
                "POST /login HTTP/1.1\n" +
                        "Host: localhost\n";

        // when
        Request simpleReq = new Request(req);

        // then
        assertThat(simpleReq.method).isEqualTo(Request.Method.POST);
        assertThat(simpleReq.path).isEqualTo("/login");
        assertThat(simpleReq.queryParam).isEmpty();
    }
    @Test
    @DisplayName("Method와 Path 생성자로 SimpleReq를 생성한다")
    void createWithMethodAndPath() {
        // when
        Request simpleReq = new Request(Request.Method.PUT, "/resource");

        // then
        assertThat(simpleReq.method).isEqualTo(Request.Method.PUT);
        assertThat(simpleReq.path).isEqualTo("/resource");
        assertThat(simpleReq.queryParam).isEmpty();
    }

    @Test
    @DisplayName("요청 문자열이 null이면 예외를 던진다")
    void throwExceptionWhenRequestIsNull() {
        assertThatThrownBy(() -> new Request(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("요청이 비어있습니다.");
    }

    @Test
    @DisplayName("요청 문자열이 blank이면 예외를 던진다")
    void throwExceptionWhenRequestIsBlank() {
        assertThatThrownBy(() -> new Request("   "))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("요청이 비어있습니다.");
    }
    @Test
    @DisplayName("요청 헤더 첫 줄 형식이 잘못되면 예외를 던진다")
    void throwExceptionWhenInvalidFirstLine() {
        // given
        String req = "GET /only-two-parts\n";

        // when & then
        assertThatThrownBy(() -> new Request(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("헤더의 첫 줄이 예상하지 못한 형식으로 들어왔습니다.");
    }
    @Test
    @DisplayName("지원하지 않는 HTTP Method면 예외를 던진다")
    void throwExceptionWhenInvalidMethod() {
        // given
        String req =
                "OPTIONS /test HTTP/1.1\n" +
                        "Host: localhost\n";

        // when & then
        assertThatThrownBy(() -> new Request(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("알맞지 않은 method입니다.");
    }
}
