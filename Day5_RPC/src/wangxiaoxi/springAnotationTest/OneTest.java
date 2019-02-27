package wangxiaoxi.springAnotationTest;

import org.springframework.stereotype.Component;


public class OneTest {

    public OneTest(){
        System.out.println("one");
    }

    public OneTest(String one){
        System.out.println("hello, "+one);
    }
}
