package testForIo.testForSimpleHttpByNIO.json.tokenizer;

import java.io.IOException;
import java.io.Reader;

/**
 * 将reader中的数据以放入内存的方式慢慢读取，自己定义了几个读取方法
 */
public class CharReader {
    private static  final int BUFFER_SIZE = 1024;
    private Reader reader;
    private char[]buffer;
    private int pos;
    private int size;
    public CharReader(Reader reader){
        this.reader = reader;
        buffer = new char[BUFFER_SIZE];
    }

    /**
     * 返回已读的最后一个字符
     * @return
     * @throws IOException
     */
    public char peek() throws IOException {
        if(pos - 1 >= size){
            return (char) -1;
        }
        return buffer[Math.max(0,pos - 1)];
    }

    public char next() throws IOException {
        if(!hasMore()){
            return (char) -1;
        }
        return buffer[pos++];
    }

    public void back(){
        pos = Math.max(0,--pos);
    }
    /**
     * 判断buffer是否已经读取完成，若完成则再像buffer中存数据
     * @return
     * @throws IOException
     */
    public boolean hasMore() throws IOException {
        if(pos < size){
            return true;
        }
        fillBuffer();
        return pos < size;
    }

    /**
     * 将reader中的内容存进buffer数组并更新，pos和size
     * @throws IOException
     */
    public void fillBuffer() throws IOException {
        int n = reader.read(buffer);
        if(n == -1){
            return;
        }
        pos = 0;
        size = n;
    }

}
