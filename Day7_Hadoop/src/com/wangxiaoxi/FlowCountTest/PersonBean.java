package com.wangxiaoxi.FlowCountTest;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PersonBean implements WritableComparable{

    /**
     * 需要封装的字段，月消费和月流量
     */
    private Double consume;
    private Double flow;

    public PersonBean(){};

    public PersonBean(Double consume,Double flow){
        this.consume = consume;
        this.flow = flow;
    }

    public Double getConsume() {
        return consume;
    }

    public void setConsume(Double consume) {
        this.consume = consume;
    }

    public Double getFlow() {
        return flow;
    }

    public void setFlow(Double flow) {
        this.flow = flow;
    }

    @Override
    public String toString() {
        return "consume:" + consume +"\t"+"flow:" + flow;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeDouble(consume);
        dataOutput.writeDouble(flow);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.consume = dataInput.readDouble();
        this.flow = dataInput.readDouble();
    }

    @Override
    public int compareTo(Object o) {
       PersonBean bean = (PersonBean)o;
       //如果this的flow大，则返回正数，则需要排在前面，在前面加一个-1，则将正数变成负数，则不用排在前面
       return (int)(-1*(this.flow - bean.getFlow()));
       //return this.flow > bean.getFlow()?-1:1;
    }
}
