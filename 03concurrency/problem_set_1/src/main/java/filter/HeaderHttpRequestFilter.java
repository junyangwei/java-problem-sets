package filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * HTTP 请求请求头过滤器
 * @author junyangwei
 * @date 2021-10-09
 */
public class HeaderHttpRequestFilter implements HttpRequestFilter {

    /**
     * 过滤方法（重写 HttpRequestFilter 接口的 filter 方法）
     * @param fullHttpRequest 完整的 HTTP 请求
     * @param ctx netty 的通道处理器的上下文
     */
    public void filter(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
        // 为请求头添加 "mal" 和 "soul"
        fullHttpRequest.headers().set("mao", "soul");
    }

}
