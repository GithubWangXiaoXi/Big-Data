package RjoinTest;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class InfoBean implements Writable {

    /**
     *  order:order_id date product_id amount
     *  product:product_id product_name category_id price
     *  infoBean是两张表（order和product）的结合
     */

    private String order_id;
    private String date;
    private String product_id;
    private Integer amount;
    private String product_name;
    private String category_id;
    private Double price;
    //order表（flag=0）和product表（flag=1）的标识
    private String flag;

    public InfoBean(){};

    public void set(String order_id, String date, String product_id, Integer amount, String product_name, String category_id, Double price, String flag) {
        this.order_id = order_id;
        this.date = date;
        this.product_id = product_id;
        this.amount = amount;
        this.product_name = product_name;
        this.category_id = category_id;
        this.price = price;
        this.flag = flag;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    /**
     *  order:order_id date product_id amount
     *  product:product_id product_name category_id price
     *  infoBean是两张表（order和product）的结合
     */
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(order_id);
        dataOutput.writeUTF(date);
        dataOutput.writeUTF(product_id);
        dataOutput.writeInt(amount);
        dataOutput.writeUTF(product_name);
        dataOutput.writeUTF(category_id);
        dataOutput.writeDouble(price);
        dataOutput.writeUTF(flag);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        //字符串以UTF的方式序列化(Text的Writable编码是UTF-8的形式)
        this.order_id = dataInput.readUTF();
        this.date = dataInput.readUTF();
        this.product_id = dataInput.readUTF();
        this.amount = dataInput.readInt();
        this.product_name = dataInput.readUTF();
        this.category_id = dataInput.readUTF();
        this.price = dataInput.readDouble();
        this.flag = dataInput.readUTF();
    }

    @Override
    public String toString() {
        return order_id+","+date+","+product_id+","+amount+","+product_name+","+category_id+","+price;
    }
}
