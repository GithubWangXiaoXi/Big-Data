package wangxiaoxi.proxyTest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

public class ProxySale {

    public <T> T getProxy(int discountCoupon,Class interfaces,Class saleServiceImpl){
        return (T) Proxy.newProxyInstance(interfaces.getClassLoader(), new Class[]{interfaces}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //注意method是原来SaleService中的方法，要调用service原来的方法得到值，然后对值做处理
                Integer price = (Integer) method.invoke(saleServiceImpl.newInstance(),args);
                Object finalPrice = price - discountCoupon;
                return finalPrice;
            }
        });
    }
}
