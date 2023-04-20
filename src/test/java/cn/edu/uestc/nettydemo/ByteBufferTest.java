package cn.edu.uestc.nettydemo;

import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
@Slf4j
public class ByteBufferTest {
    public static void main(String[] args) {
        try {
            RandomAccessFile file = new RandomAccessFile("D:\\JAVA\\Springboot+\\Netty\\code\\Netty\\netty-demo\\hello.txt","rw");
            FileChannel fileChannel = file.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(16);
            while(true){
                int len = fileChannel.read(byteBuffer);
                log.info("读到的字节数={}",len);
                if (len == -1){
                    break;
                }
                //切换读写模式为读模式
                byteBuffer.flip();
                while (byteBuffer.hasRemaining()){
                    //读出来
                    log.debug("{}",(char)byteBuffer.get());
                }
                //切换模式
                byteBuffer.clear();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
