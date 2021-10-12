package router;

import lombok.Getter;

/**
 * @author junyangwei
 * @date 2021-10-10
 */
@Getter
public enum ApiTagEnum {
    DEFAULT("/", "http://127.0.0.1:8800/"),
    TEST01("/test01api", "http://127.0.0.1:8801/"),
    TEST02("/test02api", "http://127.0.0.1:8002/");

    /**
     * 后端服务 uri 前缀标记
     */
    private String apiTag;
    /**
     * 后端服务 host
     */
    private String apiHost;

    ApiTagEnum(String apiTag, String apiHost) {
        this.apiTag = apiTag;
        this.apiHost = apiHost;
    }
}
