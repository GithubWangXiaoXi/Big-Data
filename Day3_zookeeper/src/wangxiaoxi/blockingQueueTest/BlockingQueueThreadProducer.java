package wangxiaoxi.blockingQueueTest;

import java.util.concurrent.BlockingQueue;

public class BlockingQueueThreadProducer extends Thread{

    BlockingQueue blockingQueue = null;

    public BlockingQueueThreadProducer(BlockingQueue queue){
        blockingQueue = queue;
    }

    @Override
    public void run() {
        for (int i = 0;i<5;i++){
            try {
                String name = Thread.currentThread().getName();
                //对BlockingQueue队列进行put插入操作 （如果队列满，则阻塞）
                blockingQueue.put(name+"生产的"+i);
                System.out.println(name+"生产了"+i);

                Thread.sleep((long) (Math.random()*1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
