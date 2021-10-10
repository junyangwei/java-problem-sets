package outbound.httpclient4;

import filter.HeaderHttpResponseFilter;
import filter.HttpRequestFilter;
import filter.HttpResponseFilter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import router.HttpEndpointRouter;
import router.RandomHttpEndpointRouter;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.apache.http.HttpHeaders.CONNECTION;

/**
 * @author junyangwei
 * @date 2021-10-09
 */
public class HttpOutboundHandler {
    /**
     * 定义 Http 请求工具
     */
    private CloseableHttpAsyncClient httpclient;

    /**
     * 定义指定的代理服务
     */
    private ExecutorService proxyService;

    /**
     * 定义后端服务部署的内网地址列表
     */
    private List<String> backendUrls;

    /**
     * 定义 Http 响应头过滤器
     */
    HttpResponseFilter filter = new HeaderHttpResponseFilter();

    /**
     * 定义 路由
     */
    HttpEndpointRouter router = new RandomHttpEndpointRouter();

    /**
     * HTTP 外部处理器
     * @param backendUrls 后端服务 urls
     */
    public HttpOutboundHandler(List<String> backendUrls) {
        // 赋值后端服务 urls
        this.backendUrls = backendUrls.stream()
                .map(this::formatUrl)
                .collect(Collectors.toList());

        // 获取当前 JVM 虚拟机可用核心数
        int cores = Runtime.getRuntime().availableProcessors();
        // 定义保持连接时间
        long keepAliveTime = 1000;
        // 定义队列长度
        int queueSize = 2048;

        // 定义被拒绝任务的处理器
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        // 初始化线程池，设置线程池数量为 JVM 可用核心数
        proxyService = new ThreadPoolExecutor(cores, cores,
                keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize),
                new NamedThreadFactory("proxyService"), handler);

        // 初始化 IO 反应器配置（自定义）
        IOReactorConfig ioConfig = IOReactorConfig.custom()
                .setConnectTimeout(1000)
                .setSoTimeout(1000)
                .setIoThreadCount(cores)
                .setRcvBufSize(32 * 1024)
                .build();

        // 初始化 http 请求客户端（工具）
        httpclient = HttpAsyncClients.custom().setMaxConnTotal(40)
                .setMaxConnPerRoute(8)
                .setDefaultIOReactorConfig(ioConfig)
                .setKeepAliveStrategy((response, context) -> 6000)
                .build();
        httpclient.start();
    }

    /**
     * 处理 URL（去除结尾的 / 符号）
     * @param backend 后端 url
     * @return 去除结尾的/符号的 url
     */
    private String formatUrl(String backend) {
        return backend.endsWith("/") ? backend.substring(0, backend.length() - 1) : backend;
    }

    /**
     * 请求处理方法
     * @param fullHttpRequest 完整的 HTTP 请求
     * @param ctx 通道处理器上下文
     * @param requestFilter 请求过滤器
     * @param serverPort 代理的后端服务的端口
     */
    public void handle(final FullHttpRequest fullHttpRequest,
                       final ChannelHandlerContext ctx,
                       HttpRequestFilter requestFilter, int serverPort) {
        // 获取分配到的
        String backendUrl = router.route(this.backendUrls);
        // 拼接实际请求地址
        String url = backendUrl + ":" + serverPort + fullHttpRequest.uri();
        // 通过过滤器处理请求
        requestFilter.filter(fullHttpRequest, ctx);
        // 发送请求到分配的后端服务（做了一个代理）TODO: 区分 GET 和 POST 请求
        proxyService.submit(() -> fetchGet(fullHttpRequest, ctx, url));
    }

    /**
     * 处理 GET 请求
     * @param request 完整的 HTTP 请求（已经过过滤器处理）
     * @param ctx 通道处理器上下文
     * @param url 请求 URL
     */
    private void fetchGet(final FullHttpRequest request, final ChannelHandlerContext ctx, final String url) {
        final HttpGet httpGet = new HttpGet(url);
        //httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
        httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        httpGet.setHeader("mao", request.headers().get("mao"));

        httpclient.execute(httpGet, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(final HttpResponse endpointResponse) {
                try {
                    handleResponse(request, ctx, endpointResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }

            @Override
            public void failed(final Exception ex) {
                httpGet.abort();
                ex.printStackTrace();
            }

            @Override
            public void cancelled() {
                httpGet.abort();
            }
        });
    }

    private void handleResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final HttpResponse endpointResponse) throws Exception {
        FullHttpResponse response = null;
        try {
            // 模拟成功的响应
//            String value = "Hello, world!";
//            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(value.getBytes("UTF-8")));
//            response.headers().set("Content-Type", "application/json");
//            response.headers().setInt("Content-Length", response.content().readableBytes());

            byte[] body = EntityUtils.toByteArray(endpointResponse.getEntity());
            // 打印后端服务返回的 body 及其长度
//            System.out.println(new String(body));
//            System.out.println(body.length);

            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));
            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", Integer.parseInt(endpointResponse.getFirstHeader("Content-Length").getValue()));

            // 使用响应过滤器过滤
            filter.filter(response);

            // 打印后端服务响应头
//            for (Header e : endpointResponse.getAllHeaders()) {
//                response.headers().set(e.getName(),e.getValue());
//                System.out.println(e.getName() + " => " + e.getValue());
//            }
        } catch (Exception e) {
            e.printStackTrace();
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
            exceptionCaught(ctx, e);
        } finally {
            if (fullRequest != null) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(response);
                }
            }
            ctx.flush();
            //ctx.close();
        }

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
