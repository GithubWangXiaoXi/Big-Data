package groupingComparatorTest;

import inversIndexTest.InverseIndex;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.net.URI;

public class ReachMaxPrice {

    /**
     * Order_0000001,Pdt_01,222.8
       Order_0000001,Pdt_05,25.8
       Order_0000002,Pdt_05,325.8
       Order_0000002,Pdt_03,522.8
     */

    static class ReachMaxPriceMapper extends Mapper<LongWritable,Text,OrderBean,NullWritable>{

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String content = value.toString();
            String[] tokens= content.split(",");

            String itemId = tokens[0];
            String product = tokens[1];
            Double price = Double.parseDouble(tokens[2]);

            //将数据封装成orderBean
            OrderBean orderBean = new OrderBean();
            orderBean.set(itemId,product,price);

            //orderBean为key，必须实现Comparable接口
            context.write(orderBean,NullWritable.get());
        }
    }

    static class ReachMaxPriceReducer extends Reducer<OrderBean,NullWritable,OrderBean,NullWritable> {

        @Override
        protected void reduce(OrderBean key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
            context.write(key,NullWritable.get());
        }
    }

    public static void main(String[] args) throws Exception{
        //HADOOP_HOME为空，需要配置hadoop的位置，Could not locate executable null\bin\winutils.exe in the Hadoop binaries
        System.setProperty("hadoop.home.dir","D:/programingSoftware/IDEA/Big_Data/hadoop-2.6.5");

        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

        //指定本程序的jar包所在的本地路径
        job.setJarByClass(ReachMaxPrice.class);

        //设置groupingComparator类
        job.setGroupingComparatorClass(OrderBeanGroupingComparator.class);
//        job.setPartitionerClass(ItemIdPartitioner.class);
//        job.setNumReduceTasks(2);

        //指定本业务job要使用的mapper/Reducer业务类
        job.setMapperClass(ReachMaxPriceMapper.class);
        job.setReducerClass(ReachMaxPriceReducer.class);

        job.setMapOutputKeyClass(OrderBean.class);
        job.setMapOutputValueClass(NullWritable.class);

        //指定最终输出的数据的kv类型
        job.setOutputKeyClass(OrderBean.class);
        job.setOutputValueClass(NullWritable.class);

        //注意启动main函数需要输入参数，否则会出现数组越界异常
        FileInputFormat.setInputPaths(job,new Path("D:/mapreduceTest/GrouingComparatorTest/input"));
        FileOutputFormat.setOutputPath(job,new Path("D:/mapreduceTest/GrouingComparatorTest/output"));

        //将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
        boolean b = job.waitForCompletion(true);
        System.exit(b?0:1);
    }
}
