package inbound;

import filter.HeaderHttpRequestFilter;
import filter.HttpRequestFilter;
import filter.ProxyBizFilter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import outbound.httpclient4.HttpOutboundHandler;
import outbound.netty4.NettyHttpClient;
import outbound.netty4.NettyHttpClientOutboundHandler;
import router.ApiTagEnum;
import router.HttpEndpointRouter;

import java.time.LocalDateTime;
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
     * Http 外部处理器（第三方工具）
     */
    private HttpOutboundHandler handler;

    /**
     * Netty HTTP 客户端工具
     */
    private NettyHttpClientOutboundHandler nettyHttpHandler;

    /**
     * HTTP 请求代理过滤器类对象
     */
    private HttpRequestFilter proxyBizFilter = new ProxyBizFilter();

    /**
     * Http 内部处理器构造函数
     * @param proxyServer 指定代理的服务列表
     */
    public HttpInboundHandler(List<String> proxyServer) {
        this.proxyServer = proxyServer;
        this.handler = new HttpOutboundHandler(proxyServer);
//        this.nettyHttpHandler = new NettyHttpClientOutboundHandler(proxyServer);
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
     * 通道读取数据方法，区分开请求的具体服务
     *  - uri 包含 /test01api/ 则应调用 test01 后端服务（端口为 8801）
     *  - uri 包含 /test02api/ 则应调用 test02 后端服务（端口为 8802）
     * @param ctx 通道处理器上下文
     * @param msg 完整的请求
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 打印收到请求时间
        logger.info("channelRead流量接口请求开始，时间为{}", LocalDateTime.now());
        try {
            // 将 msg 强制转换成一个 FullHttpRequest 对象，拿到它内部的结构
            FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;

            // 请求过滤器校验通过则继续，否则写入代理失败响应
            if (this.proxyFilter(fullHttpRequest, ctx)) {

                // 获取这次请求的 HTTP 协议的 URL 是什么，并打印
                String uri = fullHttpRequest.uri();
                logger.info("## 接收到新的请求，uri:{}", fullHttpRequest.uri());

                // 获取 uri 对应的 后端 HOST
                String serverHost = HttpEndpointRouter.getApiHost(uri);

                if (serverHost.equals(ApiTagEnum.DEFAULT.getApiHost())) {
                    handlerTest(fullHttpRequest, ctx);
                } else {
                    // 获取代理的后端路径
                    String url = serverHost + fullHttpRequest.uri();
                    // 发送请求到分配的后端服务（做了一个代理）TODO: 区分 GET 和 POST 请求
                    try {
                        new NettyHttpClient().connect(url, ctx);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                this.writeProxyFailResponse(ctx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    /**
     * 校验请求代理路径是否在白名单内
     * @param fullHttpRequest 请求
     * @param ctx 通道处理器上下文
     */
    private boolean proxyFilter(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
        try {
            proxyBizFilter.filter(fullHttpRequest, ctx);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 写入代理过滤失败的响应结果
     */
    private void writeProxyFailResponse(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        ctx.close();
    }

    /**
     * 处理器测试方法
     * @param fullHttpRequest 完整的 HTTP 请求
     * @param ctx 通道处理器上下文
     */
    private void handlerTest(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
        // 定义完整的 Http 响应 FullHttpResponse 对象
        FullHttpResponse response = null;
        try {
            // 测试方法的响应内容为 Hello, netty
            String value = "Hello, netty";
            // 模拟一个 Http 请求响应结果："HTTP/1.1"，"状态码为200"，"返回值UTF-8格式"
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(value.getBytes("UTF-8")));
            // 设置响应头参数：内容类型为 JSON
            response.headers().set("Content-Type", "application/json");
            // 设置响应头参数：内容长度（注意，不设置这个参数可能会导致接收方异常）
            response.headers().setInt("Content-Length", response.content().readableBytes());
        } catch (Exception e) {
            logger.error("处理测试接口出错，{}", e.getMessage());
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
