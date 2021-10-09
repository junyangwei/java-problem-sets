package filter;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * HTTP 响应头过滤器
 * @author junyangwei
 * @date 2021-10-09
 */
public class HeaderHttpResponseFilter implements HttpResponseFilter {

    @Override
    public void filter(FullHttpResponse fullHttpResponse) {
        fullHttpResponse.headers().set("kk", "java-1-nio");
    }
}
