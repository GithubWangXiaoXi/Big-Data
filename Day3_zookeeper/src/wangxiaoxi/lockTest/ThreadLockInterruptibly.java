package wangxiaoxi.lockTest;

        import java.util.concurrent.locks.ReentrantLock;

public class ThreadLockInterruptibly {

    private ReentrantLock lock = new ReentrantLock();

    public void insert(String name) throws InterruptedException {
        lock.lockInterruptibly();
        System.out.println(name+"获得锁");
        for(int i = 0;i<2000;i++){
            System.out.println(name+"-----"+i);
            //线程的run方法中不能中断，一旦中断，就有可能被其他线程抢占，使得两个线程交替运行
            Thread.sleep((long) (Math.random()*100));
        }
        System.out.println(name+"释放锁");
        lock.unlock();
    }

    public static void main(String[] args) throws Exception{

        Thread thread1 = new MyThread("thread1 ");
        Thread thread2 = new MyThread("thread2 ");

        thread1.start();
        thread2.start();

        //让主线程休眠一段时间，防止两个线程都没拿到锁，就将两个线程中断了
        Thread.sleep((long) (Math.random()*1000));

        //   如果thread1没有获取锁，此时thread1在等待interruptibly锁，则thread1可以被中断
        //中断后会该线程会抛出异常
        thread2.interrupt();
        thread1.interrupt();

    }
}

class MyThread extends Thread{

    private ThreadLockInterruptibly threadLockInterruptibly = new ThreadLockInterruptibly();
    private String name;

    public MyThread(String name){
        this.name = name;
    }

    @Override
    public void run() {
        try {
            threadLockInterruptibly.insert(name);
        } catch (InterruptedException e) {
            System.out.println(name+"被中断了");
        }
    }
}

