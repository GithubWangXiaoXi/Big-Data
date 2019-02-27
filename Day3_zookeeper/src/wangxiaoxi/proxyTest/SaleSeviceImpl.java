package wangxiaoxi.proxyTest;

public class SaleSeviceImpl implements SaleService{
    @Override
    public int yifu(Integer size) {
        System.err.println("欢迎来到王小希经营店");
        return 50;
    }
}
