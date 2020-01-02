package testForNettyIM.six.protocol.command;

import lombok.Data;
import testForNettyIM.six.protocol.Packet;

@Data
public class LoginRequestPacket extends Packet {
    private Integer userId;
    private String username;
    private String password;
    @Override
    public Byte getCommand() {
        return Command.LOGIN_REQUEST;
    }
}
