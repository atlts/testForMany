package testForIo.testForSimpleHttpByNIO;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求头部,按照标准请求头部的格式做类就可以
 */
public class Headers {
    private String method;
    private String path;
    private String version;
    private Map<String,String>headerMap = new HashMap<>();
    public  Headers(){

    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public void set(String key,String value){
        this.headerMap.put(key,value);
    }

    public String get(String key){
        return headerMap.get(key);
    }
    @Override
    public String toString() {
        return "Headers{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", version='" + version + '\'' +
                ", headerMap=" + headerMap +
                '}';
    }
}
