package nio01.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author junyangwei
 * @date 2021-09-27
 */
public class HttpHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * 重写 channelRead 方法
     *  - 通过客户端连接 Netty 的这个通道，直接读到我们的数据
     *  - 通过 Netty 的 ChannelHandlerContext 这个参数以及第二个参数 Object msg
     *      - msg 代表本次请求所有数据包装类这样一个对象，客户端的请求，所有的数据，包括
     *        它的HTTP协议的报文相关的信息都在这里
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            // 将 msg 强制转换成一个 FullHttpRequest 对象，拿到它内部的结构
            FullHttpRequest fullRequest = (FullHttpRequest) msg;
            // 获取这次请求的 HTTP 协议的 URL 是什么
            String uri = fullRequest.uri();
            // 若 URL 包含 /test 则做特殊处理
            if (uri.contains("/test")) {
                handlerTest(fullRequest, ctx, "Hello, netty");
            } else {
                handlerTest(fullRequest, ctx, "Hello, others");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void handlerTest(FullHttpRequest fullRequest, ChannelHandlerContext ctx, String body) {
        // 组装一个 HttpResponse 对象
        FullHttpResponse response = null;
        try {
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body.getBytes()));
            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", response.content().readableBytes());
        } catch (Exception e) {
            System.out.println("处理出错：" + e.getMessage());
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
        } finally {
            if (fullRequest != null) {
                if (!HttpUtil.isKeepAlive((fullRequest))) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(response);
                }
            }
        }
    }
}
