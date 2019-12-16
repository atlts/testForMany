package testForIo.testForSimpleHttpByNIO.json.model;

import testForIo.testForSimpleHttpByNIO.json.exception.JsonTypeException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Array里面的成员可以是一个JsonObject也可以是JsonArray
 */
public class JsonArray implements Iterable{
    private List list = new ArrayList();
    public void add(Object obj){
        list.add(obj);
    }
    public Object get(int index){
        return list.get(index);
    }
    public int size(){
        return list.size();
    }
    public JsonObject getJsonObject(int index){
        Object obj = list.get(index);
        if(!(obj instanceof JsonObject)){
            throw new JsonTypeException("Type of value is not JsonObject");
        }
        return (JsonObject) obj;
    }

    public JsonArray getJsonArray(int index){
        Object obj = list.get(index);
        if(!(obj instanceof JsonArray)){
            throw new JsonTypeException("Type of value is not JsonArray");
        }
        return (JsonArray) obj;
    }
    @Override
    public Iterator iterator() {
        return list.iterator();
    }

}
