package webserver.http.domain.request;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static webserver.http.domain.request.fixture.URIFixture.fixtureWithQueryParameters;

class URITest {

    @DisplayName("queryParameter가 포함되지 않는 메시지인 경우, path와 빈 QueryParameters를 갖는 URI 객체를 생성한다.")
    @Test
    void parse_empty_query_parameter() {
        String message = "/hello";
        URI actual = URI.from(message);
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(new URI("/hello"));
    }

    @DisplayName("queryParameter가 포함된 메시지인 경우, path와 빈 QueryParameters를 갖는 URI 객체를 생성한다.")
    @Test
    void parse() {
        String message = "/hello?name=jordy";
        URI actual = URI.from(message);
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(fixtureWithQueryParameters("/hello", "name", "jordy"));
    }

    @DisplayName("Parameter에 저장된 key 에 해당하는 value를 가져온다.")
    @ParameterizedTest
    @MethodSource("ProvideForGetParameter")
    void getParameter(Parameters parameters, String expected) {
        String actual = new URI("/path", parameters).getParameter("key");
        assertThat(actual).isEqualTo(expected);
    }

    public static Stream<Arguments> ProvideForGetParameter() {
        return Stream.of(
                arguments(
                        new Parameters(
                                Map.of(
                                        "키", Lists.list("밸류"),
                                        "key", Lists.list("value")
                                )
                        ), "value"
                ),
                arguments(
                        new Parameters(
                                Map.of(
                                        "키", Lists.list("밸류"),
                                        "key", Lists.list("value2", "value3")
                                )
                        ), "value2"
                ),
                arguments(
                        new Parameters(
                                Map.of(
                                        "키", Lists.list("밸류"),
                                        "key", Lists.list()
                                )
                        ), null
                ),
                arguments(
                        new Parameters(
                                Map.of(
                                        "키", Lists.list("밸류")
                                )
                        ), null
                )
        );
    }


    @DisplayName("Parameters를 추가")
    @Test
    void addParameters() {
        Parameters originalParameters = new Parameters(
                new HashMap<>(
                    Map.of(
                            "id", Lists.list("mint")
                    )
                )
        );
        URI uri = new URI("/path", originalParameters);

        Parameters target = new Parameters(Map.of(
                "name", Lists.list("jordy"),
                "age", Lists.list("20")
        ));

        uri.addParameters(target);

        assertThat(uri).usingRecursiveComparison()
                .isEqualTo(expectedURI());
    }

    private URI expectedURI() {
        Parameters addedParameters = new Parameters(Map.of(
                "id", Lists.list("mint"),
                "name", Lists.list("jordy"),
                "age", Lists.list("20")
        ));

        return new URI("/path", addedParameters);
    }
}