package testForNettyIM.four;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class NettyServer {
    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootStrap = new ServerBootstrap();//引导类
        serverBootStrap.group(bossGroup,workerGroup).//设置监听和工作线程组
                channel(NioServerSocketChannel.class)//指定IO模型为NIO编程模型，当然BIO也可以但是显然没有必要
                .handler(new ChannelInitializer<NioServerSocketChannel>() {//处理一些在服务端启用过程中的逻辑，一般用不上
                    protected void initChannel(NioServerSocketChannel ch){
                        System.out.println("===Server is starting===");
                    }
                })
                .attr(AttributeKey.newInstance("serverName"),"nettyServer")//给服务端的channel配置一个map属性，到时候可以通过channel.attr(key)取出来，一般也用不上
                .childAttr(AttributeKey.newInstance("clientKey"),"clientValue")//为每一条连接指定自定义属性
                .childOption(ChannelOption.SO_KEEPALIVE,true)//childOption为每条连接设置一些TCP底层相关的属性
                .childOption(ChannelOption.TCP_NODELAY,true)
                .option(ChannelOption.SO_BACKLOG,1024)//为服务端的channel设置一些属性
                .childHandler(new ChannelInitializer<NioSocketChannel>() {//频道初始化主要定义每条新连接数据的读写逻辑和顺序
                    protected void initChannel(NioSocketChannel ch){
                    }
                });

        bind(serverBootStrap,135);
    }

    /**
     * 当一个端口绑定失败时自动向上绑定另一个端口
     * @param serverBootstrap
     * @param port
     */
    private static void bind(final ServerBootstrap serverBootstrap,final int port){
        serverBootstrap.bind(port).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if(future.isSuccess()){
                    System.out.println("端口[" + port + "]绑定成功");
                }else{
                    System.err.println("端口[" + port + "]绑定失败");
                    bind(serverBootstrap,port + 1);
                }
            }
        });
    }



}
