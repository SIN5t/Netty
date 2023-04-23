package cn.edu.uestc.nettydemo.c4;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Server {
    public static void blockingModeSingleThread01() throws IOException {
        //使用nio理解阻塞模式，单线程
        //0. 创建byteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);

        //1. 创建服务器
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //2. 绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress(8080));

        //3. 连接的集合
        List<SocketChannel> channels = new ArrayList<>();

        while(true){
            //4. accept 建立与客户端的连接，SocketChannel用来与客户端之间通信
            log.debug("connecting...");

            //阻塞方法，只有和客户端那边建立连接，才会继续分配时间片，否则线程就停止运行了！
            SocketChannel socketChannel = serverSocketChannel.accept();

            log.debug("connected..{}",socketChannel);
            channels.add(socketChannel);
            for(SocketChannel channel : channels){
                log.debug("before read...{}",channel);

                //阻塞方法，只有channel中有数据，才会继续指向下去，否则线程到这里就停止了
                channel.read(byteBuffer);

                byteBuffer.flip();
                log.info("读取的内容：{}",byteBuffer);
                byteBuffer.clear();
                log.debug("after read...{}", channel);

            }

        }




    }
}
