package com.wangxiaoxi.FlowCountTest;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

import java.util.HashMap;

/**
 * partitioner的key是map task输出的key
 *              value是map task输出的value
 * 需求：是通过手机号码来划分省份
 */
public class ProvincePartitioner extends Partitioner<Text,PersonBean> {

    //用hashmap来对号码子串进行分组操作
    private static HashMap<String,Integer> phoneProvince = new HashMap<>();

    static{
        phoneProvince.put("134",0);
        phoneProvince.put("135",1);
        phoneProvince.put("136",2);
        phoneProvince.put("137",3);
        phoneProvince.put("138",4);
    }

    @Override
    public int getPartition(Text text, PersonBean personBean, int i) {
        //只获取号码的前3位
        String prefix = text.toString().substring(0,3);
        Integer provinceId = phoneProvince.get(prefix);
        return provinceId == null ? 5 : provinceId;
    }
}
