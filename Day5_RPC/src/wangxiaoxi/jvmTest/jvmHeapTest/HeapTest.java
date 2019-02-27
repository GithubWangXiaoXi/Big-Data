package wangxiaoxi.jvmTest.jvmHeapTest;

import java.util.ArrayList;
import java.util.List;

public class HeapTest {

    static class Person{
        public String[] names = new String[1024*60*40];
    }

    public static void fillHeap(int num) throws Exception{
        List<Person> people = new ArrayList<>();
        for(int i = 0;i<num;i++){
            Thread.sleep(50);
            people.add(new Person());
        }
    }

    public static void main(String[] args) throws Exception{
        Thread.sleep(20000);
        fillHeap(100);
        Thread.sleep(20000);
    }
}
