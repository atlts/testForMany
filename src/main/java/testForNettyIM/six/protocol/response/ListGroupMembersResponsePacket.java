package testForNettyIM.six.protocol.response;

import lombok.Data;
import testForNettyIM.six.protocol.Packet;
import testForNettyIM.six.session.Session;

import java.util.List;

import static testForNettyIM.six.protocol.command.Command.LIST_GROUP_MEMBERS_RESPONSE;

@Data
public class ListGroupMembersRequestPacket extends Packet {
    private String groupId;

    private List<Session> sessionList;
    @Override
    public Byte getCommand() {
        return LIST_GROUP_MEMBERS_RESPONSE;
    }
}
