package wangxiaoxi.synchronizedTest;

public class ThreadSynchronized {

    public static void main(String[] args) {
        ThreadSynchronized threadSynchronized1 = new ThreadSynchronized();
        ThreadSynchronized threadSynchronized2 = new ThreadSynchronized();

        //创建线程的匿名内部类，注意创建该类后记得start启动线程
        new Thread("thread1"){
            @Override
            public void run() {
                //  threadSynchronized1为两个线程访问的锁对象（同一时间内只有一个线程只能使用这个锁对象）
                //没有获得锁对象的线程在外面等待
                synchronized (threadSynchronized1){
                    int j = 1/0;
                    for(int i = 0;i < 10;i++){
                        try {
                            Thread.sleep((long) (Math.random()*1000));
                            System.out.println(currentThread().getName()+"-----i:"+i);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();

        new Thread("thread2"){
            @Override
            public void run() {
                synchronized (threadSynchronized1){
                    for(int i = 0;i < 10;i++){
                        try {
                            Thread.sleep((long) (Math.random()*1000));
                            System.out.println(currentThread().getName()+"-----i:"+i);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }
}
