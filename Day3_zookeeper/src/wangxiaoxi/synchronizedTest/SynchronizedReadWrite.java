package wangxiaoxi.synchronizedTest;

public class SynchronizedReadWrite {

    //同步代码块来解决读写操作问题（只能同步一个锁对象）
    private static Integer money = 10000;

    //读操作
    public void ReadInBank(){
        synchronized (money){
            System.out.println(Thread.currentThread().getName()+"开始读取现金");
            System.out.println(money);
            System.out.println(Thread.currentThread().getName()+"读取完毕");
            System.out.println();
        }
    }

    //写操作
    public void WriteInBank(){
        synchronized (money){
            System.out.println(Thread.currentThread().getName()+"开始更改现金");
            money = money + 1000;
            System.out.println(Thread.currentThread().getName()+"更改完毕,现金为:"+money);
            System.out.println();
        }
    }

    public static void main(String[] args) {

        Thread thread1 = new ReadThread();
        Thread thread2 = new ReadThread();
        Thread thread3 = new ReadThread();

        Thread thread4 = new WriteThread();

        thread1.start();
        thread2.start();
        thread3.start();

        thread4.start();
    }
}

class WriteThread extends Thread{

    SynchronizedReadWrite synchronizedReadWrite = new SynchronizedReadWrite();

    @Override
    public void run() {
        for (int i =0;i < 5;i++){
            synchronizedReadWrite.WriteInBank();
            //在进入同步代码块之前先休眠（这样读写都能抢占）
            try {
                Thread.sleep((long) (Math.random()*1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class ReadThread extends Thread{

    SynchronizedReadWrite synchronizedReadWrite = new SynchronizedReadWrite();

    @Override
    public void run() {
        for (int i = 0;i<5;i++){
            synchronizedReadWrite.ReadInBank();
            try {
                Thread.sleep((long) (Math.random()*1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
