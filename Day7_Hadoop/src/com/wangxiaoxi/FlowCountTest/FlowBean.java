package com.wangxiaoxi.FlowCountTest;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 对象实现Writable接口，能实现序列化
 */
public class FlowBean implements Writable{

    private Long upFlow;
    private Long downFlow;
    private Long sumFlow;

    public FlowBean(){};

    public FlowBean(Long upFlow,Long downFlow){
        this.upFlow = upFlow;
        this.downFlow = downFlow;
        this.sumFlow = upFlow + downFlow;
    }

    public Long getUpFlow() {
        return upFlow;
    }

    public void setUpFlow(Long upFlow) {
        this.upFlow = upFlow;
    }

    public Long getDownFlow() {
        return downFlow;
    }

    public void setDownFlow(Long downFlow) {
        this.downFlow = downFlow;
    }

    @Override
    public String toString() {
        return "upFlow:"+upFlow+"\t"+"downFlow:"+downFlow+"\t"+"sumFlow"+sumFlow;
    }

    /**
     * 序列化
     */
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeLong(upFlow);
        dataOutput.writeLong(downFlow);
        dataOutput.writeLong(sumFlow);
    }

    /**
     * 反序列化
     */
    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.upFlow = dataInput.readLong();
        this.downFlow = dataInput.readLong();
        this.sumFlow = dataInput.readLong();
    }
}
