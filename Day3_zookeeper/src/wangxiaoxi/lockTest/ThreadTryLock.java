package wangxiaoxi.lockTest;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadTryLock {
    public static void main(String[] args) {

        Lock lock = new ReentrantLock();

        new Thread("thread1"){
            @Override
            public void run() {

                //使用trylock()来获取锁,tryLock()不一定能获取锁，所以释放锁的前提是有锁
                //所以try,catch,finally要在判断里面
                boolean status = lock.tryLock();
                System.out.println(getName() + "是否获得锁:" + status);
                if (status) {
                    try {
                        System.out.println(getName() + "获得锁");
                        Thread.sleep((long) (Math.random() * 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        //在finally中记得手动释放锁，避免死锁
                        System.out.println(getName() + "释放锁");
                        lock.unlock();
                    }
                }
            }
        }.start();

        new Thread("thread2"){
            @Override
            public void run() {

                //注意两线程用的是同一把锁
                boolean status = lock.tryLock();
                System.out.println(getName() + "是否获得锁:" + status);
                if (status) {
                    try {
                        System.out.println(getName() + " start");
                        Thread.sleep((long) (Math.random() * 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println(getName() + " release");
                        lock.unlock();
                    }
                }
            }
        }.start();
    }
}
