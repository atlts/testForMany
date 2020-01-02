package testForZooKeeper;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class TicketSeller {

    private void sell(){
        System.out.println("Start selling tickets");
        //线程随机休眠
        int sleepMills = (int)(Math.random() * 2000);
        try{
            Thread.sleep(sleepMills);//模仿处理了一段逻辑
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Selling over");
    }
    public void sellThicketWithLock() throws KeeperException, InterruptedException, IOException {
        LockSample lock = new LockSample();
        lock.acquireLock();
        sell();
        lock.releaseLock();
    }
    public static class Test extends Thread{
        public void run(){
            try {
                new TicketSeller().sellThicketWithLock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        TicketSeller ticketSeller = new TicketSeller();
        for(int i = 0;i < 10;i++){
           Test test = new Test();
           test.start();
        }
    }
}
