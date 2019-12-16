package testForIo.testForSimpleHttpByNIO.json.model;

import testForIo.testForSimpleHttpByNIO.json.exception.JsonTypeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存储一个完整的JsonObject加上对Json里面的tokens查询，增改操作
 * 所谓Json里面的token就是以冒号前为键，冒号后为值的键值对
 */
public class JsonObject {
    private Map<String, Object> map = new HashMap<>();

    public void put(String key, Object value) {
        map.put(key, value);
    }

    public Object get(String key) {
        return map.get(key);
    }

    public List<Map.Entry<String, Object>> getAllKeyValue() {
        return new ArrayList<>(map.entrySet());
    }

    public JsonObject getJsonObject(String key) {
        if (!map.containsKey(key)) {
            throw new IllegalArgumentException("Invalid Key");
        }
        Object obj = map.get(key);
        if (!(obj instanceof JsonObject)) {
            throw new JsonTypeException("Type of value is not JsonObject");
        }
        return (JsonObject) obj;
    }

    @Override
    public String toString() {
        return "JsonObject{" +
                "map=" + map +
                '}';
    }
}
