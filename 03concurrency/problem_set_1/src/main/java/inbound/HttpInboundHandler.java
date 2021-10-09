package inbound;

import filter.HeaderHttpRequestFilter;
import filter.HttpRequestFilter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import outbound.httpclient4.HttpOutboundHandler;

import java.util.Date;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Http 内部处理器
 * @author junyangwei
 * @date 2021-10-09
 */
public class HttpInboundHandler extends ChannelInboundHandlerAdapter {
    /**
     * 日志打印 Logger 类对象
     */
    private static Logger logger = LoggerFactory.getLogger(HttpInboundHandler.class);

    /**
     * 代理服务列表
     */
    private final List<String> proxyServer;

    /**
     * Http 外部处理器
     */
    private HttpOutboundHandler handler;

    /**
     * Http 请求过滤器类对象
     */
    private HttpRequestFilter filter = new HeaderHttpRequestFilter();

    /**
     * Http 内部处理器构造函数
     * @param proxyServer 指定代理的服务列表
     */
    public HttpInboundHandler(List<String> proxyServer) {
        this.proxyServer = proxyServer;
        this.handler = new HttpOutboundHandler(proxyServer);
    }

    /**
     * 通道读取数据完毕后处理方法
     * @param ctx 通道处理器上下文
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * 通道读取数据方法
     * @param ctx 通道处理器上下文
     * @param msg 完整的请求
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String startTime = new Date().toString();
        try {
            logger.info("channelRead流量接口请求开始，时间为{}", startTime);
            FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
            String uri = fullHttpRequest.uri();
            logger.info("接收到的请求url为{}", uri);
            if (uri.contains("/test")) {
                handlerTest(fullHttpRequest, ctx);
            }

            handler.handle(fullHttpRequest, ctx, filter);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    /**
     * 处理器测试方法
     * @param fullHttpRequest 完整的 HTTP 请求
     * @param ctx 通道处理器上下文
     */
    private void handlerTest(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
        // 定义完整的 Http 响应
        FullHttpResponse response = null;
        try {
            // 测试方法的响应内容为 Hello, netty
            String value = "Hello, netty";
            // 模拟一个 Http 请求响应结果："HTTP/1.1"，"状态码为200"，"返回值UTF-8格式"
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(value.getBytes("UTF-8")));
        } catch (Exception e) {
            logger.error("处理测试接口出错", e);
            // 若失败，同样模拟一个 Http 请求响应结果："HTTP/1.1"，"状态码为204"
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
        } finally {
            // 最后的处理逻辑，当 fullHttpRequest 存在时进行
            if (fullHttpRequest != null) {
                // 校验当前 Http 请求连接状态
                if (!HttpUtil.isKeepAlive(fullHttpRequest)) {
                    // 若不再连接，则直接关闭通道的监听状态
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    // 否则，设置响应头：连接仍然存在
                    response.headers().set(CONNECTION, KEEP_ALIVE);
                    // 对通道处理器上下文，写入响应内容
                    ctx.write(response);
                }
            }
        }
    }
}
