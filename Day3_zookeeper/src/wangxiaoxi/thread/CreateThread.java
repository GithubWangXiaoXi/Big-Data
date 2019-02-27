package wangxiaoxi.thread;

import org.junit.Test;

public class CreateThread {

    @Test
    public void ThreadRun(){

        Thread thread1 = new MyThread();
        Thread thread2 = new MyThread();
        thread1.start();
        thread2.start();
    }

    public static void main(String[] args) {

//        Thread thread1 = new MyThread();
//        Thread thread2 = new MyThread();
//        thread1.start();
//        thread2.start();

        Thread thread3 = new Thread(new MyThread1("a"));
        Thread thread4 = new Thread(new MyThread1("b"));
        thread3.start();
        thread4.start();
    }
}

class MyThread1 implements Runnable{

    private String flag;
    public MyThread1(String flag){
         this.flag = flag;
    }

    @Override
    public void run(){
        for(int i = 0;i < 10;i++){
            try {
                Thread.sleep((long) (Math.random()*1000));
                System.out.println(Thread.currentThread().getName()+"----"+flag+"-----i:"+i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class MyThread extends Thread{

    @Override
    public void run(){
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

