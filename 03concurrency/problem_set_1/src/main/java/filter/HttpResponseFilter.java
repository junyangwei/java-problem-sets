package filter;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * HTTP 响应过滤器接口
 * @author junyangwei
 * @date 2021-10-09
 */
public interface HttpResponseFilter {
    /**
     * 过滤方法
     * @param fullHttpResponse 完整的响应
     */
    void filter(FullHttpResponse fullHttpResponse);
}
