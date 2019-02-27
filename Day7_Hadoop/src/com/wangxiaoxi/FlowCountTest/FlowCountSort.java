package com.wangxiaoxi.FlowCountTest;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class FlowCountSort {

    /**
     * 需要对之前输出的手机号，月总流量的数据文件进行月总流量的降序排序
     * 输入的key：Long
     *      value：Text
     * 输出的key：PersonBean
     *      value：Text
     */
    static class  FlowCountSortMapper extends Mapper<LongWritable,Text,PersonBean,Text>{

        PersonBean personBean = new PersonBean();
        /**
         * 之前计算总流量和总消费的数据如下
         * 134434676621	consume:276.9	flow:1404.0
         134547114238	consume:298.9	flow:318.6
         134751629866	consume:184.2	flow:1480.1
         134809774605	consume:277.0	flow:694.0
         135713172586	consume:87.3	flow:283.8
         135816971422	consume:96.0	flow:1617.4
         135827395761	consume:34.3	flow:1908.5
         */
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String content = value.toString();
            String[] tokens = content.split("\t");

            int index = tokens[2].indexOf(":");
            Double flowSum = Double.parseDouble(tokens[2].substring(index+1));

            String phoneNum = tokens[0];

            int index1 = tokens[1].indexOf(":");
            Double consume = Double.parseDouble(tokens[1].substring(index1+1));

            personBean.setFlow(flowSum);
            personBean.setConsume(consume);
            context.write(personBean,new Text(phoneNum));
        }
    }

    static class FlowCountSortReducer  extends Reducer<PersonBean,Text,Text,PersonBean>{
        //在写reduce方法之前，需要在PersonBean中实现WritableComparable接口，这样可以bean实现序列化和bean的key的排序
        @Override
        protected void reduce(PersonBean key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            context.write(values.iterator().next(),key);
        }
    }

    public static void main(String[] args) throws Exception {

        //HADOOP_HOME为空，需要配置hadoop的位置，Could not locate executable null\bin\winutils.exe in the Hadoop binaries
        System.setProperty("hadoop.home.dir","D:/programingSoftware/IDEA/Big_Data/hadoop-2.6.5");

        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

        //指定本程序的jar包所在的本地路径
        job.setJarByClass(FlowCountSort.class);

        //指定本业务job要使用的mapper/Reducer业务类
        job.setMapperClass(FlowCountSort.FlowCountSortMapper.class);
        job.setReducerClass(FlowCountSort.FlowCountSortReducer.class);

        //指定mapper输出数据的kv类型
        job.setMapOutputKeyClass(PersonBean.class);
        job.setMapOutputValueClass(Text.class);

        //指定最终输出的数据的kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(PersonBean.class);

        //注意启动main函数需要输入参数，否则会出现数组越界异常
        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        //将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
        boolean b = job.waitForCompletion(true);
        System.exit(b?0:1);
    }
}
