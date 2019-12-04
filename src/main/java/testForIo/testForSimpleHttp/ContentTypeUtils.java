package testForIo.testForSimpleHttp;

import testForIo.testForSimpleHttp.json.JSONParser;
import testForIo.testForSimpleHttp.json.model.JsonObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 根据使用的文件名，确定相应的Content-Type
 *
 */
public class ContentTypeUtils {
    private static JsonObject jsonObject;
    private static final String JSON_PATH = "src/main/resources/static/meta/content-type.json";

    /**
     * 把json文件转为jsonObject的形式也是就把该变成map形式的东西都变成map
     */
    static{
        JSONParser jsonParser = new JSONParser();
        try{
            jsonObject = (JsonObject)jsonParser.fromJSON(readFile());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 利用文件通道读出JSON_PATH中的数据并转化为字符串
     * @return
     */
    private static String readFile(){
        try{
            RandomAccessFile raf = new RandomAccessFile(JSON_PATH,"r");
            FileChannel channel = raf.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate((int)channel.size());
            buffer.clear();
            channel.read(buffer);
            buffer.flip();
            return new String(buffer.array());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据文件名确定Content-Type
     * @param ext
     * @return
     */
    public static String getContentType(String ext){
        return (String)jsonObject.get(ext);
    }
}
