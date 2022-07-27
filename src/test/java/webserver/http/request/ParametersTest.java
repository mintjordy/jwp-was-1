package webserver.http.request;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ParametersTest {

    @DisplayName("Parameters를 인자로 받아 내부의 저장한 파라미터정보를 모두 저장한다.")
    @Test
    void add() {
        HashMap<String, List<String>> params = new HashMap<>();
        params.put("key1", Lists.list("value1"));
        Parameters parameters = new Parameters(params);

        HashMap<String, List<String>> targetParams = new HashMap<>();
        targetParams.put("key2", Lists.list("value2"));
        targetParams.put("key3", Lists.list("value3"));
        Parameters target = new Parameters(targetParams);

        parameters.add(target);

        assertThat(parameters).usingRecursiveComparison()
                .isEqualTo(expected());
    }

    private Parameters expected() {
        HashMap<String, List<String>> params = new HashMap<>();
        params.put("key1", Lists.list("value1"));
        params.put("key2", Lists.list("value2"));
        params.put("key3", Lists.list("value3"));

        return new Parameters(params);
    }

    @DisplayName("key 에 해당하는 Parameter 값을 가져온다. 값이 여러개가 있는경우, 첫번째 값을 가져온다.")
    @ParameterizedTest
    @MethodSource("ProvideForGet")
    void get(Parameters parameters, String expected) {
        String actual = parameters.get("key");
        assertThat(actual).isEqualTo(expected);
    }

    public static Stream<Arguments> ProvideForGet() {
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
}