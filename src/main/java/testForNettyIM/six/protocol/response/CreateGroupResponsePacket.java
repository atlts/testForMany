package testForNettyIM.six.protocol.response;

import lombok.Data;
import testForNettyIM.six.protocol.Packet;

import java.util.List;

import static testForNettyIM.six.protocol.command.Command.CREATE_GROUP_RESPONSE;

@Data
public class CreateGroupResponse extends Packet {
    private boolean success;

    private String groupId;

    private List<String> userNameList;
    @Override
    public Byte getCommand() {
        return CREATE_GROUP_RESPONSE;
    }
}
