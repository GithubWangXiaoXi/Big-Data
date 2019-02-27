package groupingComparatorTest;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class OrderBeanGroupingComparator extends WritableComparator {

    public OrderBeanGroupingComparator() {
        //必须指明反射的类为OrderBean，rpc通信
        super(OrderBean.class,true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        OrderBean abean = (OrderBean) a;
        OrderBean bbean = (OrderBean) b;

        //如果orderbean的itemId相同，则reduce视为该bean是相同的
        //如果a的id大，返回正数，则a排在后面，（a排在前面，后面都无所谓，只要相同的id到同一个reduce就行）
        return abean.getItemId().compareTo(bbean.getItemId());
    }
}
