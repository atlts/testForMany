package testForIo.testForSimpleHttpByNIO.json;

import testForIo.testForSimpleHttpByNIO.json.model.JsonObject;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import java.nio.channels.FileChannel;


public class JsonTest {
    public static void main(String[] args) throws IOException {
        String path = "src/main/resources/static/meta/testForJson.json";
        RandomAccessFile raf = new RandomAccessFile(path,"r");
        FileChannel channel = raf.getChannel();

        ByteBuffer buffer= ByteBuffer.allocate((int) channel.size());
        buffer.clear();
        channel.read(buffer);
        buffer.flip();
        String str = new String(buffer.array());
        JsonObject jsonObject = new JsonObject();
        jsonObject =(JsonObject) new JSONParser().fromJSON(str);
        System.out.println(jsonObject);
    }
}
