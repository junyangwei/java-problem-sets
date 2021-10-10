package router;

import java.util.List;

/**
 * @author junyangwei
 * @date 2021-10-09
 */
public interface HttpEndpointRouter {

    String route(List<String> endpoints);

    /**
     * 获取 uri 对应的后端服务端口（其实还可以扩展到前端的应用）
     * @param uri 请求的 uri
     * @return 端口字符串
     */
    static int getApiPort(String uri) {
        if (uri.contains(ApiTagEnum.TEST01.getApiMatch())) {
            return ApiTagEnum.TEST01.getPort();
        } else if (uri.contains(ApiTagEnum.TEST02.getApiMatch())) {
            return ApiTagEnum.TEST02.getPort();
        } else {
            return ApiTagEnum.DEFAULT.getPort();
        }
    }

    // Load Balance
    // Random
    // RoundRibbon
    // Weight
    // - server01,20
    // - server02,30
    // - server03,50
}
