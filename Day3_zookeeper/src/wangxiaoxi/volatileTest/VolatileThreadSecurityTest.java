package wangxiaoxi.volatileTest;

public class VolatileThreadSecurityTest {

    private static volatile int money = 0;

    public static void main(String[] args) throws InterruptedException {

        for(int i = 0;i<100;i++){
            new Thread(){
                @Override
                public void run() {
                    for(int j = 0;j<1000;j++){
                        money++;
                    }
                }
            }.start();
        }

        Thread.sleep(2000);
        System.out.println(money);
        System.out.println("real:"+100*1000);
    }
}
