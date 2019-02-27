package wangxiaoxi.lockTest;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadLock {

    public static void main(String[] args) {

        Lock lock = new ReentrantLock();

        new Thread("thread1"){
            @Override
            public void run(){
                try {
                    //使用lock()来获取锁
                    lock.lock();
                    for(int i = 0;i < 10;i++){
                        Thread.sleep((long) (Math.random()*1000));
                        System.out.println(currentThread().getName()+"-----i:"+i);
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    //在finally中记得手动释放锁，避免死锁
                    lock.unlock();
                }
            }
        }.start();

        new Thread("thread2"){
            @Override
            public void run(){
                try {
                    //注意两线程用的是同一把锁
                    lock.lock();
                    for(int i = 0;i < 10;i++){
                        Thread.sleep((long) (Math.random()*1000));
                        System.out.println(currentThread().getName()+"-----i:"+i);
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    lock.unlock();
                }
            }
        }.start();
    }
}

