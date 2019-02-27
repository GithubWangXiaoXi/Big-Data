package wangxiaoxi.springAnotationTest;

import org.springframework.stereotype.Component;

@Component
public class TwoTest {

    public TwoTest(){
        System.out.println("two");
    }

    public TwoTest(String two){
        System.out.println("hello, "+two);
    }
}
