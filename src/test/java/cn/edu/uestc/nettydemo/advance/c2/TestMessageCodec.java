package cn.edu.uestc.nettydemo.advance.c2;

import cn.edu.uestc.nettydemo.message.LoginRequestMessage;
import cn.edu.uestc.nettydemo.protocal.MessageCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

/**
 * * 入站处理器通常是 Channel==Inbound==HandlerAdapter 的子类，主要用来读取客户端数据，写回结果
 * * 出站处理器通常是 Channel==Outbound==HandlerAdapter 的子类，主要对写回结果进行加工
 */
public class TestMessageCodec {

    public static void main(String[] args) throws Exception {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LoggingHandler(),//日志的输出
                new LengthFieldBasedFrameDecoder(
                        1024,12,4,0,0
                ),
                new MessageCodec()
        );
        //encode 测试
        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("张三","123456");
        //embeddedChannel.writeOutbound(loginRequestMessage);//想要让消息出栈(发出去)，会经过MessageCode，出栈的时候会调用encode，编码为byte字节发出去


        //想测试解码，就需要入栈（解码进来的消息，这个消息是已经encode过的了）
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(); //
        new MessageCodec().encode(null,loginRequestMessage,buf);//手动调用encode

        embeddedChannel.writeInbound(buf);//入栈处理器(收消息，要先解码，处理消息后再发出去)就是要写入buf,就是解码

    }


}
