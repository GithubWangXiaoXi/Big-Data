package wangxiaoxi.springAnotationTest;

import org.springframework.stereotype.Component;

@Component
public class ThreeTest {

    public ThreeTest(){
        System.out.println("three");
    }

    public ThreeTest(String three){
        System.out.println("hello, "+three);
    }
}
