package groupingComparatorTest;

import org.apache.avro.Schema;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class OrderBean implements WritableComparable<OrderBean>{

    private String itemId;
    private String product;
    private Double price;

    public void set(String itemId, String product,Double price) {
        this.itemId = itemId;
        this.product = product;
        this.price = price;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    @Override
    public int compareTo(OrderBean o) {
        int result = this.getItemId().compareTo(o.getItemId());

        //如果itemId相同，则比较price，price大的排前面，所以需要在前面加‘-’号
        if(result == 0){
            result = -this.getPrice().compareTo(o.getPrice());
        }
        return result;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(itemId);
        dataOutput.writeDouble(price);
        dataOutput.writeUTF(product);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.itemId = dataInput.readUTF();
        this.price = dataInput.readDouble();
        this.product = dataInput.readUTF();
    }

    public String toString(){
        return itemId+"\t"+product+"\t"+price+"\n";
    }
}
