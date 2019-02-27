package wangxiaoxi.jvmTest.jvmThreadTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadTest {

    public static void threadRunPersisitenly(){
        new Thread(){
            @Override
            public void run() {
                System.out.println("threadRunPersisitenly");
                while (true){}
            }
        }.start();
    }

    public static void threadWaitPersisitently(){

        ReentrantLock reentrantLock = new ReentrantLock();
        new Thread(){
            @Override
            public void run() {

                synchronized (reentrantLock){
                    System.out.println("threadWaitPersisitenly");
                    try {
                        reentrantLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public static void main(String[] args) throws IOException {
       BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
       bufferedReader.readLine();
       threadRunPersisitenly();
       threadWaitPersisitently();
    }
}
