import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.util.DigestUtils;
import redis.clients.util.MurmurHash;
import sun.security.provider.MD5;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TestForMurmurHash {
    public static void testForTime(){
        MurmurHash murmurHash = new MurmurHash();
        long st = System.currentTimeMillis();
        Map<String,Boolean> map = new HashMap<String, Boolean>();
        for(int i = 0;i < 100000;i++) {
            String str = new Random(100).toString();
            murmurHash.hash(str);

        }
        System.out.println("murmurHash一万次Hash用时:   " + (System.currentTimeMillis() - st));
        st = System.currentTimeMillis();
        for(int i = 0;i < 100000;i++) {
            String str = new Random(100).toString();
            DigestUtils.md5DigestAsHex(str.getBytes());
        }
        System.out.println("MD5一万次Hash用时:   " + (System.currentTimeMillis() - st));
    }
    public static void testForRepeat(){
        MurmurHash murmurHash = new MurmurHash();
        Map<Long,Boolean> map = new HashMap<Long, Boolean>();
        int sum = 0;
        for(int i = 0;i < 1000000;i++) {
            String str = new Random(100).toString();
            long mur = murmurHash.hash(str);
            if(map.containsKey(mur)){
                sum++;
            }
            else
            map.put(mur,true);
        }
        System.out.println("murmurHash一百万次Hash长度为100的随机字符串重复:   " + (double)sum/10000.0);
        sum = 0;
        Map<String,Boolean>map1 = new HashMap<String, Boolean>();
        for(int i = 0;i < 1000000;i++) {
            String str = new Random(100).toString();
            String mur = DigestUtils.md5DigestAsHex(str.getBytes());
            if(map1.containsKey(mur)){
                sum++;
            }
            else
            map1.put(mur,true);
        }
        System.out.println("MD5一百万次Hash长度为100的随机字符串重复:   " + (double)sum/10000.0);
    }
    public static void main(String[] args) {
        testForTime();
        testForRepeat();
    }

}
