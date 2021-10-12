package filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import router.ApiTagEnum;

/**
 * 在接收来自客户端的请求时的过滤器
 * @author junyangwei
 * @date 2021-10-10
 */
public class ProxyBizFilter implements HttpRequestFilter {
    /**
     * 重写filter方法，做一个后端服务白名单过滤器
     *  - 只给：/test01api/ 以及 /test02api/
     */
    @Override
    public void filter(FullHttpRequest request, ChannelHandlerContext ctx) {
        String uri = request.uri();

        boolean validUri = uri.startsWith(ApiTagEnum.TEST01.getApiTag())
                || uri.startsWith(ApiTagEnum.TEST02.getApiTag())
                || ApiTagEnum.DEFAULT.getApiTag().equals(uri);

        if (!validUri) {
            throw new RuntimeException("[ERROR] 不支持的uri:" + uri);
        }

        request.headers().add("biz-tag", "uri-valid");
    }
}
