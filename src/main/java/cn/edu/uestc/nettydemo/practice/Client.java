package cn.edu.uestc.nettydemo.practice;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Scanner;

@Slf4j
public class Client {
    public static void main(String[] args) {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(2);

        ChannelFuture channelFuture = new Bootstrap()
                .group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        log.info("init...");
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                log.info(byteBuf.toString(Charset.defaultCharset()));
                            }
                        });
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));

        log.info("channelFuture = {}",channelFuture.getClass().toString());
        //connect方法异步非阻塞

        //在另外一个线程而不是主线程中完成任务,(ChannelFutureListener)会在连接建立的时候被调用
        channelFuture.addListener((ChannelFutureListener) future -> {
            Channel channel = future.channel();
            log.info("当前线程不是主线程，是：{}",Thread.currentThread().getName());

            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while(true){
                    String inputStr = scanner.nextLine();
                    if ("q".equals(inputStr)) {
                        //这时候应该关闭连接
                        channel.close(); //这个是异步操作！
                        //channel关闭后想要 优雅地关闭 nioEventLoopGroup,不能在这里，因为close是异步的方法！
                        //解决方案：使用从channel中获取closeFuture对象
                        break;
                    }
                    channel.writeAndFlush(inputStr);
                    ChannelFuture closeFuture = channel.closeFuture();
            /*log.debug("waiting close...");
            closeFuture.sync();
            log.debug("处理关闭之后的操作");*/
                    closeFuture.addListener((ChannelFutureListener) channelFuture1 -> {
                        log.info("当前线程是：{}",Thread.currentThread().getName());
                        //这是lambda的写法，实际上new ChannelFutureListener()，重写方法
                        nioEventLoopGroup.shutdownGracefully();
                    });
                }
            }).start();

        });



    }
}
