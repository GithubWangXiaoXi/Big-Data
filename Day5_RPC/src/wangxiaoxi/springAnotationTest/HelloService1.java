package wangxiaoxi.springAnotationTest;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

@RPCService("helloService1")
public class HelloService1{

    public void helloService1(String hello){
        System.out.println("Hello,this is hello Service1"+hello);
    }
}
