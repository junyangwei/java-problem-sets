package router;

import lombok.Getter;

/**
 * @author junyangwei
 * @date 2021-10-10
 */
@Getter
public enum ApiTagEnum {
    DEFAULT(8800, "/"),
    TEST01(8801, "/test01api/"),
    TEST02(8802, "/test02api/");

    private int port;
    private String apiMatch;

    ApiTagEnum(int port, String apiMatch) {
        this.port = port;
        this.apiMatch = apiMatch;
    }
}
