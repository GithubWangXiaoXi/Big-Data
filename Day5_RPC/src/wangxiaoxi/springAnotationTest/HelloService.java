package wangxiaoxi.springAnotationTest;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@RPCService("helloService")
public class HelloService implements ApplicationContextAware{

    public void helloService(String hello){
        System.out.println("Hello,this is hello Service "+hello);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //通过自定义的注解来获取javaBean对象，并保存在hashMap中
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RPCService.class);
        System.out.println("beansWithAnnotation:"+beansWithAnnotation);

        Set<String> stringSet = beansWithAnnotation.keySet();
        System.out.println("keyset:"+stringSet);

        //获取自定义注解中的类对象集合
        Collection<Object> values = beansWithAnnotation.values();
        System.out.println("values:"+values);

        //循环获取自定义注解中的类对象
        for(Object bean:values){
            //打印注解上的值
            String value = bean.getClass().getAnnotation(RPCService.class).value();
            System.out.println(value);

            try {
                //通过方法名和参数类型获取方法
                Method method = bean.getClass().getMethod("helloService",new Class[]{String.class});
                Object result = method.invoke(bean, new Object[]{"hello"});
                System.out.println(result);

                Method method1= bean.getClass().getMethod("helloService1",new Class[]{String.class});
                Object result1 = method1.invoke(bean, new Object[]{"hello"});
                System.out.println(result1);
            } catch (Exception e) {
                 e.printStackTrace();
            }
        }
    }
}
