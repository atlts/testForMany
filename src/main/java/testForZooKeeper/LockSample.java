package testForZooKeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;



import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class LockSample {
    private ZooKeeper zkClient;
    private static final String LOCK_ROOT_PATH = "/LOCKs";
    private static final String LOCK_NODE_NAME = "LOCK_";
    private String lockPath;
    private Watcher watcher = new Watcher(){
        @Override
        public void process(WatchedEvent event){
            System.out.println(event.getPath() + " 前锁释放");
            synchronized (this){
                notifyAll();
            }
        }
    };

    /**
     * 监听
     * @throws IOException
     */
    public LockSample() throws IOException, KeeperException, InterruptedException {
        zkClient = new ZooKeeper("localhost:2181", 10000, new org.apache.zookeeper.Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == org.apache.zookeeper.Watcher.Event.KeeperState.Disconnected) {
                    System.out.println("失去连接");
                }
            }
        });
    }
        /**
         *
         */
        public void acquireLock() throws KeeperException, InterruptedException {
            createLock();
            attemptLock();

        }
        private void createLock() throws KeeperException, InterruptedException {
            Stat stat = zkClient.exists(LOCK_ROOT_PATH,false);//如果根结点不存在则创建根结点
            if(stat == null){
                zkClient.create(LOCK_ROOT_PATH,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }
            //创建EPHEMERAL_SEQUENTIAL 类型的节点
            String lockPath = zkClient.create(LOCK_ROOT_PATH + "/" + LOCK_NODE_NAME,
                    Thread.currentThread().getName().getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(Thread.currentThread().getName() + " 锁创建： " + lockPath);
            this.lockPath = lockPath;
        }

        private void attemptLock() throws KeeperException, InterruptedException {
            List<String> lockPaths = null;
            lockPaths = zkClient.getChildren(LOCK_ROOT_PATH,false);
            Collections.sort(lockPaths);//获得所有有序节点的排序

            int index = lockPaths.indexOf(lockPath.substring(LOCK_ROOT_PATH.length()  +1));
            if(index == 0){//即当前线程的lockPath排在最前面
                System.out.println(Thread.currentThread().getName() + " 获得锁，lockPath：" +
                        lockPath);
                return;
            }else{
                String preLockPath = lockPaths.get(index - 1);
                Stat stat = zkClient.exists(LOCK_ROOT_PATH + "/" + preLockPath, watcher);
                if(stat == null){//如果前一个线程完事了
                    attemptLock();
                }else{
                    System.out.println(Thread.currentThread().getName() + " 等待当前锁释放preLockPath: " + preLockPath);
                    synchronized (watcher){
                        watcher.wait();
                    }
                    attemptLock();
                }
            }
        }

        public void releaseLock() throws InterruptedException, KeeperException {
            zkClient.delete(lockPath,-1);
            zkClient.close();
            System.out.println( lockPath + " 锁释放");
        }

}
