package wangxiaoxi.springAnotationTest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringDriver {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("/spring.xml");
    }
}
