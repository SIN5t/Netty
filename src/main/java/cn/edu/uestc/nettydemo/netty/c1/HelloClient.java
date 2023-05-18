package cn.edu.uestc.nettydemo.netty.c1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class HelloClient {
    public static void main(String[] args) {

        //1.启动类
        try {
            new Bootstrap()
                    // 2. 添加EventLoop
                    .group(new NioEventLoopGroup())
                    //3. 选择客户端channel实现
                    .channel(NioSocketChannel.class)
                    // 4.添加处理器
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override //在建立连接之后才被调用
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast(new StringEncoder());
                        }
                    })
                    // 5.连接到服务器
                    .connect(new InetSocketAddress("localhost",8080))
                    .sync()
                    .channel()
                    .writeAndFlush("Hello World!");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }



}
