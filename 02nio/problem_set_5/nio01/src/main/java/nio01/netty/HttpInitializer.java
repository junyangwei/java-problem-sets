package nio01.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * Http 初始化类
 *  - 继承了 ChannelInitializer
 * @author junyangwei
 * @date 2021-09-27
 */
public class HttpInitializer extends ChannelInitializer<SocketChannel> {
    /**
     * 重写父类的 initChannel 方法
     * @param ch
     */
    @Override
    public void initChannel(SocketChannel ch) {
        // 通过 SocketChannel 获取它的管道
        ChannelPipeline p = ch.pipeline();
        // 添加 HttpServer 的编码器
        p.addLast(new HttpServerCodec());
        // 再添加一个报文聚合器 HttpObjectAggregator
        p.addLast(new HttpObjectAggregator(1024 * 1024));
        // 最后添加一个 HttpHandler 自定义的处理器
        p.addLast(new HttpHandler());
    }
}
