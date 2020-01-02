package testForNettyIM.six.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import testForNettyIM.six.protocol.response.ListGroupMembersResponsePacket;

public class ListGroupResponseHandler extends SimpleChannelInboundHandler<ListGroupMembersResponsePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ListGroupMembersResponsePacket msg) throws Exception {
        System.out.println("群[" + msg.getGroupId() + "]中的人有：" + msg.getSessionList());
    }
}
