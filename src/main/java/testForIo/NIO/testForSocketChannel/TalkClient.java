package testForIo.NIO.testForSocketChannel;

import sun.nio.ch.sctp.SctpNet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

import static testForIo.NIO.testForSocketChannel.Utils.recvMsg;
import static testForIo.NIO.testForSocketChannel.Utils.sendMsg;

public class TalkClient {
    private static final String EXIT_MARK = "exit";
    private String hostname;
    private int port;
    TalkClient(String hostname,int port){
        this.hostname = hostname;
        this.port = port;
    }

    public void start() throws IOException {
        //打开一个套接字通道，并向服务器发起连接
        SocketChannel channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(hostname,port));

        Scanner sc = new Scanner(System.in);

        while(true){
            System.out.println("请输入： ");
            String msg = sc.nextLine();
            sendMsg(channel,msg);
            if(EXIT_MARK.equals(msg)){
                break;
            }
            System.out.println("请等待服务端回信...");
            msg = recvMsg(channel);
            System.out.println("\n服务端：");
            System.out.println(msg + "\n");
        }
        channel.close();
    }

    public static void main(String[] args) throws IOException {
        new TalkClient("localhost",8888).start();
    }
}
