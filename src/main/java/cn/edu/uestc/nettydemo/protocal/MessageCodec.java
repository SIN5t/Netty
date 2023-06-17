package cn.edu.uestc.nettydemo.protocal;

import cn.edu.uestc.nettydemo.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@Slf4j
//@ChannelHandler.Sharable
public class MessageCodec extends ByteToMessageCodec<Message> {


    /**
     * ByteToMessageCodec<Message>将bytebuffer和我们自定义的消息进行转换，那么自定义的消息类型就是Message
     */
    @Override
    public void encode(ChannelHandlerContext ctx, Message msg, ByteBuf byteBufOut) throws Exception {
        //1. 自定义四字节的魔数，比如Java的：Java baby
        byteBufOut.writeBytes(new byte[]{1,2,3,4});
        //2. 1字节的版本，
        byteBufOut.writeByte(1);
        //3. 1个字节的序列化方式  自己规定： 0代表jdk序列化方式，1代表json
        byteBufOut.writeByte(0);
        //4.1 字节的指令类型  是登入还是其他等等，在message那个类中进行了定义
        byteBufOut.writeByte(msg.getMessageType());
        //5. 4个字节
        byteBufOut.writeInt(msg.getSequenceId());
        // 无意义，对其填充;  0xff 表示为二进制 11111111
        byteBufOut.writeByte(0xff);
        //6. 获取内容的字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        //7. 长度
        byteBufOut.writeInt(bytes.length);
        // 8. 写入内容
        byteBufOut.writeBytes(bytes);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBufIn, List<Object> list) throws Exception {
        int magicNum = byteBufIn.readInt(); //读四个字节的魔数
        byte version = byteBufIn.readByte(); ///读版本号
        byte serializerType = byteBufIn.readByte();
        byte messageType = byteBufIn.readByte();
        int sequenceId = byteBufIn.readInt();
        byteBufIn.readByte();//填充符号
        int length = byteBufIn.readInt();
        byte[] bytes = new byte[length];
        byteBufIn.readBytes(bytes,0,length);
        //将bytes反序列化为对象
        if (serializerType == 0){
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));//和之前编码是反着来的，现在先用byteStream，再转为对象流
            Message message = (Message) ois.readObject();
            log.info("{},{},{},{},{},{}",magicNum,version,serializerType,messageType,serializerType,length);
            log.info("{}",message);
            //结果放到形参的list中，是为了给下一个handler用
            list.add(message);
        }

    }
}
