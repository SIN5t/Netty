package cn.edu.uestc.nettydemo.practice;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * 写一个双向通信，客户端发什么，server就回复什么，eco
 */
@Slf4j
public class Server {
    public static void main(String[] args) throws InterruptedException {
        //1、启动类
        ChannelFuture channelFuture = new ServerBootstrap()
                .group(new NioEventLoopGroup(1), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //nioSocketChannel.pipeline().addLast(new StringDecoder());// 将 ByteBuf 转换为字符串
                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = msg instanceof ByteBuf ? (ByteBuf) msg : null;
                               /* if (byteBuf != null) {
                                    byte[] bytes = new byte[16];
                                    byteBuf.readBytes(bytes, 0, byteBuf.readableBytes());
                                    log.debug(new String(bytes));
                                }*/
                                log.info(byteBuf.toString(Charset.defaultCharset()));

                                //ctx.writeAndFlush(msg);
                                // 建议使用 ctx.alloc() 创建 ByteBuf
                                ByteBuf response = ctx.alloc().buffer();
                                response.writeBytes(byteBuf);
                                ctx.writeAndFlush(response);
                            }
                        });


                    }
                }).bind(8080);



    }


}
