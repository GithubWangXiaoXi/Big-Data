package com.wangxiaoxi.FlowCountTest;

        import org.apache.hadoop.conf.Configuration;
        import org.apache.hadoop.fs.Path;
        import org.apache.hadoop.io.IntWritable;
        import org.apache.hadoop.io.LongWritable;
        import org.apache.hadoop.io.Text;
        import org.apache.hadoop.mapred.lib.HashPartitioner;
        import org.apache.hadoop.mapreduce.Job;
        import org.apache.hadoop.mapreduce.Mapper;
        import org.apache.hadoop.mapreduce.Partitioner;
        import org.apache.hadoop.mapreduce.Reducer;
        import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
        import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

        import java.io.IOException;

public class FlowCount {

    /**
     * 输入数据:key: 偏移量，默认类型为Long
     *         value: 一行的文本内容
     * 输出数据:key: 手机号码
     *         value: 有上行流量和下行流量的bean对象
     */
    static class FlowCountMapper extends Mapper<LongWritable,Text,Text,PersonBean>{
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            /**
             * 得到一行文本内容，通过tab分隔符对数据进行特征抽取
             */
            String content = value.toString();
            String tokens[] = content.split("\t");

            /**
             *  数据是这样的
             *  166920837514	165.9	1643.3
             166728577083	182	    578.9
             166450197279	201.5	1520.1
             */

            String phoneNum = tokens[0];
            double consume = Double.parseDouble(tokens[1]);
            double flow = Double.parseDouble(tokens[2]);
            context.write(new Text(phoneNum),new PersonBean(consume,flow));
        }
    }

    /**
     * 输入数据: key:手机号码
     *          value: 上行流量和下行流量的bean对象
     * 输出数据: key:手机号码
     *          value: 上行流量和下行流量的bean对象
     */
    static class FlowCountReducer extends Reducer<Text,PersonBean,Text,PersonBean>{

        //reduce task进程对每一组相同的key的<key,value>的map组只调用一次reduce方法（相同key才reduce）
        @Override
        protected void reduce(Text key, Iterable<PersonBean> values, Context context) throws IOException, InterruptedException {

            //key值
            //Long l = 1;会报错
            //Long l = 1L; 正确
            Double totalConsume = 0.0;
            Double totalFlow = 0.0;

            for(PersonBean bean : values){
                totalConsume += bean.getConsume();
                totalFlow += bean.getFlow();
            }

            //将相同的key（手机号），进行月消费，月流量统计（相同的key才需要reduce）
            context.write(new Text(key),new PersonBean(totalConsume,totalFlow));
        }
    }

    public static void main(String[] args) throws Exception {

        //HADOOP_HOME为空，需要配置hadoop的位置，Could not locate executable null\bin\winutils.exe in the Hadoop binaries
        System.setProperty("hadoop.home.dir","D:/programingSoftware/IDEA/Big_Data/hadoop-2.6.5");

        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

//        //设置partitioner为ProvincePartitioner
//        job.setPartitionerClass(ProvincePartitioner.class);
//
//        //设置reduce task的并行度（与partitioner相适应）
//        job.setNumReduceTasks(6);

        //指定本程序的jar包所在的本地路径
        job.setJarByClass(FlowCount.class);

        //指定本业务job要使用的mapper/Reducer业务类
        job.setMapperClass(FlowCountMapper.class);
        job.setReducerClass(FlowCountReducer.class);

        //指定mapper输出数据的kv类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(PersonBean.class);

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
