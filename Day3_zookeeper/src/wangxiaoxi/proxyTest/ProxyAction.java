package wangxiaoxi.proxyTest;

import javax.xml.ws.Service;

public class ProxyAction {

    public static void main(String[] args) {

        ProxySale proxySale = new ProxySale();
        System.out.println("折扣店，大甩卖");
        //这是创建代理对象(代理对象是接口类型)，不是调用yifu方法，方法需要invoke
//        Integer price = proxySale.getProxy(10,SaleService.class,SaleSeviceImpl.class);
        SaleService proxy = proxySale.getProxy(10,SaleService.class,SaleSeviceImpl.class);

        Integer price = proxy.yifu(50);
        System.out.println("衣服成交价:"+price);
    }
}
