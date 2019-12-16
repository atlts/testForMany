package testForIo.testForSimpleHttpByNIO;

import testForIo.testForSimpleHttpByNIO.exception.InvalidHeaderException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.time.Instant;
import java.util.*;

import static testForIo.testForSimpleHttpByNIO.StatusCodeEnum.*;

/**
 * 显然这就是服务器
 */
public class HttpServer {
    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static final String INDEX_PAGE = "hello.html";
    private static final String STATIC_RESOURCE_DIR = "src/main/resources/static";
    private static final String META_RESOURCE_DIR_PREFIX = "/meta/";//为了测试403错误码设置的路径
    private static final String KEY_VALUE_SEPARATOR = ":";
    private static final String CRLF = "\r\n";//Enter是用这个符号，表示换行并且回到行首，头部字段都用这个

    private int port;

    public HttpServer() {
        this(DEFAULT_PORT);
    }

    public HttpServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress("localhost", port));
        ssc.configureBlocking(false);//设置ssc为不阻塞通道

        System.out.println(String.format("HttpServer 已启动，正在监听 ： " + port + " 端口"));
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int readyNum = selector.select();//在selector观察的通道都没有收到信息时会阻塞
            if (readyNum == 0) {
                continue;
            }

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectionKeys.iterator();
            while (it.hasNext()) {
                SelectionKey selectionKey = it.next();
                it.remove();

                if (selectionKey.isAcceptable()) {
                    SocketChannel socketChannel = ssc.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {
                    request(selectionKey);
                    selectionKey.interestOps(SelectionKey.OP_WRITE);
                } else if (selectionKey.isWritable()) {
                    response(selectionKey);
                }
            }
        }
    }

    public void handleForbidden(SocketChannel channel) {
        try {
            handleError(channel, FORBIDDEN.getCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将selectionKey中的通道的数据读出来,这也就是客户端发来的请求
     *
     * @param selectionKey
     */
    public void request(SelectionKey selectionKey) throws IOException {
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
        channel.read(buffer);

        buffer.flip();
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        String headStr = new String(bytes);
        try {
            Headers headers = parseHeader(headStr);
            selectionKey.attach(Optional.of(headers));//将通道中的数据暂存入selectionKey当中，等到需要处理的时候再拿出来
        } catch (InvalidHeaderException e) {//当发现http请求无效时，则会发送500错误码
            selectionKey.attach(Optional.empty());
        }
    }

    /**
     * 将Http请求的内容整理好,放入headers中
     *
     * @param headerStr
     * @return
     */
    private Headers parseHeader(String headerStr) {
        if (Objects.isNull(headerStr) || headerStr.isEmpty()) {
            throw new InvalidHeaderException();
        }
        int index = headerStr.indexOf(CRLF);
        if (index == -1) {
            throw new InvalidHeaderException();
        }
        Headers headers = new Headers();
        String firstLine = headerStr.substring(0, index);
        String[] parts = firstLine.split(" ");
        /**
         * 请求的第一部分由三部分组成，METHOD，PATH，VERSION
         * 比如  GET /hello.html  HTTP/1.1
         */
        if (parts.length < 3) {
            throw new InvalidHeaderException();
        }

        headers.setMethod(parts[0]);
        headers.setPath(parts[1]);
        headers.setVersion(parts[2]);
        parts = headerStr.split(CRLF);
        /**
         * 获取HTTP请求的其余部分
         */
        for (String part : parts) {
            index = part.indexOf(KEY_VALUE_SEPARATOR);
            if (index == -1) {
                continue;
            }
            String key = part.substring(0, index);
            if (index + 1 >= part.length()) {
                headers.set(key, "");
                continue;
            }
            String value = part.substring(index + 1);
            headers.set(key, value);
        }
        return headers;
    }

    private void response(SelectionKey selectionKey) throws IOException {
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        Optional<Headers> op = (Optional<Headers>) selectionKey.attachment();//selectionKey上attach的信息

        //无效请求，格式错误什么的请求，返回400错误
        if (!op.isPresent()) {
            handleBadRequest(channel);
            channel.close();
            return;
        }

        String ip = channel.getRemoteAddress().toString().replace("/", "");
        Headers headers = op.get();
        /**
         * 处理403请求，对请求资源的访问被服务器拒绝
         */
        if (headers.getPath().startsWith(META_RESOURCE_DIR_PREFIX)) {
            handleForbidden(channel);
            channel.close();
            log(ip, headers, FORBIDDEN.getCode());
            return;
        }

        try {
            handleOK(channel, headers.getPath());
            log(ip, headers, OK.getCode());
        } catch (FileNotFoundException e) {
            handleNotFound(channel);
            log(ip, headers, NOT_FOUND.getCode());
        } catch (Exception e) {
            handleInternalServerError(channel);
            log(ip, headers, INTERNAL_SERVER_ERROR.getCode());
        } finally {
            channel.close();
        }
    }

    private void handleNotFound(SocketChannel channel) {
        try {
            handleError(channel, NOT_FOUND.getCode());
        } catch (Exception e) {
            handleInternalServerError(channel);
        }
    }


    private void handleInternalServerError(SocketChannel channel) {
        try {
            handleError(channel, INTERNAL_SERVER_ERROR.getCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleOK(SocketChannel channel, String path) throws IOException {
        ResponseHeaders headers = new ResponseHeaders(OK.getCode());

        ByteBuffer bodyBuffer = readFile(path);
        headers.setContentLength(bodyBuffer.capacity());
        headers.setContentType(ContentTypeUtils.getContentType(getExtension(path)));
        ByteBuffer headBuffer = ByteBuffer.wrap(headers.toString().getBytes());

        channel.write(new ByteBuffer[]{headBuffer, bodyBuffer});
    }

    private String getExtension(String path) {
        if (path.endsWith("/")) {
            return "html";
        }
        String filename = path.substring(path.lastIndexOf("/") + 1);
        int index = filename.lastIndexOf(".");
        return index == -1 ? "*" : filename.substring(index + 1);
    }

    private void handleBadRequest(SocketChannel channel) {
        try {
            handleError(channel, BAD_REQUEST.getCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleError(SocketChannel channel, int statusCode) throws IOException {
        ResponseHeaders headers = new ResponseHeaders(statusCode);

        ByteBuffer bodyBuffer = readFile(String.format("/%d.html", statusCode));
        headers.setContentLength(bodyBuffer.capacity());
        headers.setContentType(ContentTypeUtils.getContentType("html"));
        ByteBuffer headerBuffer = ByteBuffer.wrap(headers.toString().getBytes());
        channel.write(new ByteBuffer[]{headerBuffer, bodyBuffer});//将响应头部和内容写入频道
    }

    private ByteBuffer readFile(String path) throws IOException {
        path = STATIC_RESOURCE_DIR + (path.endsWith("/") ? path + INDEX_PAGE : path);
        RandomAccessFile raf = new RandomAccessFile(path, "r");
        FileChannel channel = raf.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
        channel.read(buffer);

        buffer.flip();
        return buffer;
    }

    private void log(String ip, Headers headers, int code) {
        String dateStr = Date.from(Instant.now()).toString();
        String msg = String.format("%s \n [%s] \n \" %s  %s  %s   \" %d \n %s\n",
                ip, dateStr, headers.getMethod(), headers.getPath(), headers.getVersion(), code, headers.get("User-Agent"));
        System.out.println(msg);
    }

    public static void main(String[] args) throws IOException {
        new HttpServer().start();
    }
}
