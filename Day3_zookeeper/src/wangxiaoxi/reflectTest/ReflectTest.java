package wangxiaoxi.reflectTest;

import org.junit.Test;
import sun.plugin2.message.Message;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectTest {

    private String name = "wangxiaoxi.reflectTest.Person";

    @Test
    public void getClassName() throws Exception {
        Class clazz = Class.forName(name);
        Person person = (Person) clazz.newInstance();
        System.out.println(person);
    }

    @Test
    public void getConstructorName() throws Exception {
        Constructor constructor = Class.forName(name).getConstructor(String.class,String.class,Integer.class);
        Person person = (Person) constructor.newInstance("wangxiaoxi","male",21);
        System.out.println(person);
    }

    @Test
    public void getMethod() throws Exception{

        Constructor constructor = Class.forName(name).getConstructor(String.class,String.class,Integer.class);
        Person person = (Person) constructor.newInstance("wangxiaoxi","male",21);

        System.out.println("get private field");
        Field field = Class.forName(name).getDeclaredField("name");
        field.setAccessible(true);
        field.set(person,"wangxiaoxi1");
        System.out.println(person);
        System.out.println("___________________________");

        System.out.println("get private function");
        Method method = Class.forName(name).getDeclaredMethod("toString");
        method.setAccessible(true);
        System.out.println(method);
        System.out.println("___________________________");

        System.out.println("get interface");
        Class[] interfaces = Class.forName(name).getInterfaces();
        System.out.println(Arrays.toString(interfaces));
    }
}
