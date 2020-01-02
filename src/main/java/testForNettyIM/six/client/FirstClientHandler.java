package testForNettyIM.six;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;
import java.util.Date;

public class FirstClientHandler extends ChannelInboundHandlerAdapter {
    public void channelActive(ChannelHandlerContext ctx){
        System.out.println(new Date() + ":客户端写出数据");

        ByteBuf buffer = getByteBuf(ctx);//获取数据

        ctx.channel().writeAndFlush(buffer);//写入数据
    }

    private ByteBuf getByteBuf(ChannelHandlerContext ctx){
        byte[] bytes = "你好，闪电侠！".getBytes(Charset.forName("utf-8"));
        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeBytes(bytes);
        return buffer;
    }

    public void channelRead(ChannelHandlerContext ctx,Object msg){
        ByteBuf byteBuf = (ByteBuf)msg;
        System.out.println(new Date() + ":客户端读到数据 ->" + byteBuf.toString(Charset.forName("utf-8")));
    }
}
