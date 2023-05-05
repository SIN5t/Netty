package cn.edu.uestc.nettydemo.netty.c1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class HelloServer {
    public static void main(String[] args) {
        //1.启动类，负责组装netty组件，启动服务器
        new ServerBootstrap()
                //2. BossEventLoop, WorkerEventLoop（selector，thread），group组
                .group(new NioEventLoopGroup())
                //3. 选择服务器的ServerSocketChannel
                .channel(NioServerSocketChannel.class)//OIO(BIO)，NIO
                // 4. boss负责连接，child（worker）负责处理读写，编解码等等,决定了worker能够执行哪些操作（Handler）
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //添加具体的Handler
                        nioSocketChannel.pipeline().addLast(new StringDecoder());// 传过来是ByteBuf字节形式，现在要解码为String
                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){//自定义的Handler
                            @Override//处理读事件
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(msg);
                            }
                        });
                    }
                    //绑定监听端口
                }).bind(8080);
    }

}
