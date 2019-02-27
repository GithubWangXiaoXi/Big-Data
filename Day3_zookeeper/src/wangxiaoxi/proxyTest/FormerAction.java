package wangxiaoxi.proxyTest;

public class FormerAction {

    public static void main(String[] args) {
        SaleService saleService = new SaleSeviceImpl();
        System.out.println("开始买衣服");
        Integer price = saleService.yifu(50);
        System.out.println("衣服成交价:"+price);
    }
}
