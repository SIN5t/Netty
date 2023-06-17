package cn.edu.uestc.nettydemo.protocal;

import com.google.common.base.Utf8;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

@Slf4j
public class HttpProtocal {

    public static void main(String[] args) {
        //单独拿出来，最后就能够统一关闭
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup woker = new NioEventLoopGroup(2);
        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .channel(NioServerSocketChannel.class)//选择服务器的ServerSocketChannel
                    .group(boss, woker)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new HttpServerCodec());//coder ,decoder 编解码一体
                            socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) throws Exception {
                                    //获取请求路径
                                    log.debug(httpRequest.uri());
                                    //返回一个响应
                                    DefaultFullHttpResponse response = new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.OK);

                                    //将要发送的请求
                                    byte[] bytes = "<h1>Hello,Http&&Netty, 电子科技大学</h1>".getBytes(StandardCharsets.UTF_8);
                                    response.headers().setInt(CONTENT_LENGTH, bytes.length);
                                    response.content().writeBytes(bytes);

                                    //写回响应
                                    ctx.writeAndFlush(response);
                                }
                            });
                        }
                    }).bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error",e);
            throw new RuntimeException(e);
        } finally {
            boss.shutdownGracefully();
            woker.shutdownGracefully();
        }


    }


}
