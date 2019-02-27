package wangxiaoxi.jvmTest.jvmThreadTest;

public class DeadLockTest extends Thread{

    private Integer a;
    private Integer b;

    public DeadLockTest(int a,int b){
        this.a = a;
        this.b = b;
    }

    @Override
    public void run(){
        synchronized (Integer.valueOf(a)){
            synchronized (Integer.valueOf(b)){
                Integer result = a+b;
                System.out.println(Thread.currentThread().getName()+result);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(5000);
        for(int i = 0;i<100;i++){
            new DeadLockTest(1,2).start();
            new DeadLockTest(2,1).start();
        }
        Thread.sleep(20000);
    }
}
