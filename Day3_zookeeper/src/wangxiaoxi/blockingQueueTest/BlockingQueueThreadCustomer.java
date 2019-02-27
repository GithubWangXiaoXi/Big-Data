package wangxiaoxi.blockingQueueTest;

import java.util.concurrent.BlockingQueue;

public class BlockingQueueThreadCustomer extends Thread{

    BlockingQueue blockingQueue = null;

    public BlockingQueueThreadCustomer(BlockingQueue queue){
        blockingQueue = queue;
    }

    @Override
    public void run() {
        for (int i = 0;i<5;i++){
            try {
                String name = Thread.currentThread().getName();
                //对BlockingQueue队列进行take提取操作（如果队列为空，则阻塞）
                String object = (String) blockingQueue.take();
                System.out.println(name+"消费了\""+object+"\"");

                Thread.sleep((long) (Math.random()*1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
