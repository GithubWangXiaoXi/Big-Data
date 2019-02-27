package wangxiaoxi.blockingQueueTest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Test {

    public static void main(String[] args) {
        //队列大小只有2
        BlockingQueue blockingQueue = new LinkedBlockingQueue(2);
        BlockingQueueThreadCustomer customer = new BlockingQueueThreadCustomer(blockingQueue);
        BlockingQueueThreadProducer producer = new BlockingQueueThreadProducer(blockingQueue);

        customer.start();
        producer.start();
    }
}
