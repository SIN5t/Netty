package cn.edu.uestc.nettydemo.practice;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class Client {
    public static void main(String[] args) {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        log.info("init...");
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));
        log.info("channelFuture = {}",channelFuture.getClass().toString());
        //connect方法异步非阻塞
        //本来没有单独拿到channelFuture的时候也要.channel，之后才能writeAndFlush
        Channel channel = channelFuture.addListener((ChannelFutureListener) ChannelFuture::sync).channel();

    }

}
