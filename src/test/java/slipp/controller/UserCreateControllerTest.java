package slipp.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import slipp.db.DataBase;
import slipp.model.User;
import slipp.service.UserCreateService;
import webserver.http.domain.Headers;
import webserver.http.domain.Protocol;
import webserver.http.domain.request.Method;
import webserver.http.domain.request.Parameters;
import webserver.http.domain.request.Request;
import webserver.http.domain.request.RequestLine;
import webserver.http.domain.request.URI;
import webserver.http.domain.response.Response;
import webserver.http.domain.response.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static webserver.http.domain.Headers.LOCATION;
import static webserver.http.domain.request.Method.GET;
import static webserver.http.domain.request.Method.POST;

class UserCreateControllerTest {
    private final UserCreateController userCreateController = new UserCreateController(new UserCreateService());

    @AfterEach
    void tearDown() {
        DataBase.clearAll();
    }

    @DisplayName("POST 요청이고 /user/create path 요청인 경우, true 반환")
    @ParameterizedTest
    @MethodSource("provideForRequires")
    void requires(Method method, String path, boolean expected) {
        RequestLine requestLine = new RequestLine(method, new URI(path), Protocol.HTTP_1_1);
        Request request = new Request(requestLine, new Headers(new LinkedHashMap<>()));

        boolean actual = userCreateController.requires(request);

        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForRequires() {
        return Stream.of(
                arguments(GET, "/user/create", false),
                arguments(POST, "/user/create", true),
                arguments(GET, "/nono.html", false)
        );
    }

    @DisplayName("parameter로 정보를 받아 회원가입처리 후 302 Response를 응답한다.")
    @Test
    void handle() {
        RequestLine requestLine = new RequestLine(
                POST,
                new URI(
                        "/user/create",
                        new Parameters(new HashMap<>(Map.of(
                                "userId", new ArrayList<>(List.of("someId")),
                                "password", new ArrayList<>(List.of("password")),
                                "name", new ArrayList<>(List.of("name")),
                                "email", new ArrayList<>(List.of("email"))
                        )))
                ),
                Protocol.HTTP_1_1);
        Request request = new Request(requestLine, new Headers(new LinkedHashMap<>()));

        Response actual = userCreateController.handle(request);

        assertThat(actual).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(new Response(
                        Status.found(),
                        new Headers(Map.of(
                                LOCATION, "/index.html"
                        )),
                        null
                ));

        Optional<User> someId = DataBase.findUserById("someId");
        assertThat(someId.isPresent()).isTrue();
        assertThat(someId.get()).usingRecursiveComparison()
                .isEqualTo(new User(
                        "someId",
                        "password",
                        "name",
                        "email"
                )
        );
    }

    @DisplayName("중복된 아이디로 회원가입을 시도하는 경우, 재로그인 페이지로 302 Response를 응답한다.")
    @Test
    void handle_duplication_user_id() {
        DataBase.addUser(new User(
                        "someId",
                        "123",
                        "이름",
                        "email@emart"
                )
        );

        RequestLine requestLine = new RequestLine(
                POST,
                new URI(
                        "/user/create",
                        new Parameters(new HashMap<>(Map.of(
                                "userId", new ArrayList<>(List.of("someId")),
                                "password", new ArrayList<>(List.of("password")),
                                "name", new ArrayList<>(List.of("name")),
                                "email", new ArrayList<>(List.of("email"))
                        )))
                ),
                Protocol.HTTP_1_1);
        Request request = new Request(requestLine, new Headers(new LinkedHashMap<>()));

        Response actual = userCreateController.handle(request);

        assertThat(actual).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(new Response(
                        Status.found(),
                        new Headers(Map.of(
                                LOCATION, "/user/form_failed.html"
                        )),
                        null
                ));
    }
}