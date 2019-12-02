package testForIo.NIO.testForSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

import static testForIo.NIO.testForSocketChannel.Utils.recvMsg;
import static testForIo.NIO.testForSocketChannel.Utils.sendMsg;


public class TalkServer {
    private static final String EXIT_MARK = "exit";

    private int port;

    TalkServer(int port){
        this.port = port;
    }

    public void start() throws IOException{
        //创建服务端套接子通道，监听端口并等待客户端连接
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(port));
        System.out.println("服务器已经启动，正在监听端口 ： " + port);
        SocketChannel channel = ssc.accept();//这样应该就和连接端口的客户端建立了连接,不过建立连接之前估计始终是阻塞状态
        System.out.println("接受来自" + channel.getRemoteAddress().toString().replace("/","") + "的请求");

        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println("请等待客户端发送消息...");
            String msg = recvMsg(channel);
            System.out.println("\n客户端：");
            System.out.println(msg + "\n");

            System.out.println("请输入： ");
            msg = sc.nextLine();
            sendMsg(channel,msg);
            if(EXIT_MARK.equals(msg)){
                break;
            }
        }


        channel.close();
        ssc.close();
    }

    public static void main(String[] args) throws IOException {
        new TalkServer(8888).start();
    }
}
