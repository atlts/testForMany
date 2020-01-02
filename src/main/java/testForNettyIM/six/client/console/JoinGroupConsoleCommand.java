package testForNettyIM.six.client.console;

import io.netty.channel.Channel;
import testForNettyIM.six.protocol.request.JoinGroupRequestPacket;

import java.util.Scanner;

public class JoinGroupConcoleCommand implements ConsoleCommand{
    @Override
    public void exec(Scanner scanner, Channel channel) {
        JoinGroupRequestPacket joinGroupRequestPacket = new JoinGroupRequestPacket();

        System.out.println("请输入群聊ID：");
        String groupId = scanner.next();

        joinGroupRequestPacket.setGroupId(groupId);
        channel.writeAndFlush(joinGroupRequestPacket);
    }
}
