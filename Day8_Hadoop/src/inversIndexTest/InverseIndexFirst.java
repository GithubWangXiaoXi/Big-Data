package inversIndexTest;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.*;

public class InverseIndexFirst {

    /**
     *
     *   统计每个文件中的单词数量
     输出结果类似： hello  a.txt->3  b.txt->2
     实现步骤：
     1次mapreduce完成：map分词，key为单词，value为a.txt，然后在reduce中对每一个单词同一个文件进行统计
     eg：hello: hello a.txt, hello a.txt, hello b.txt   hello a.txt->2 b.txt->1（先将文件名存在map中，但是有一个问题，如果有很多个文件，
     那么需要设置许多个变量来统计每个文件中单词的个数，方法不太合理）
     2次mapreduce完成：map分词，判断文件，将单词和文件写入到key中，value为单词的个数这样就统计出了每个
     文件中单词的个数。在处理输出的格式即可
     eg: first:hello: hello--a.txt 2  hello--b.txt 1   second: hello a.txt->2 b.txt->1
     */

    //将hadoop的Text的乱码的对象(Text用UTF-8解GBK的码，导致乱码)，先以GBK的形式解码，再编码成utf-8的编码形式
    public static Text transformTextToUTF8(Text text, String encoding) {
        String value = null;
        try {
            /**
             String类有构造方法: 通过使用指定的 charset 解码指定的 byte 子数组，构造一个新的 String
             text.getBytes(),会将刚才的文本对象以UTF-8的形式解码，由于之前解码的方式不对，所以需要用GBK解码
             当解码成功后的正常显示的字符串对象才可以继续用其他编码格式编码！！！。
             */
            value = new String(text.getBytes(), 0, text.getLength(), encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();}
        //这样value是正常显示的字符串对象，通过Text进行utf-8的编码
        return new Text(value);
    }


    static class InverseIndexMapper extends Mapper<LongWritable,Text,Text,IntWritable> {

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            //获取切片名称
            FileSplit split = (FileSplit) context.getInputSplit();
            String fileName = split.getPath().getName();

            String content = value.toString();

            String[] words = content.split(" ");

            //key:word value:fileName
            for(String word:words) {
                String finalWord = word+"---"+fileName;
                context.write(new Text(finalWord),new IntWritable(1));
            }
        }
    }

    static class InverseIndexReducer extends Reducer<Text,IntWritable,Text,IntWritable> {

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

              int count = 0;
              for(IntWritable value:values){
                  count += value.get();
              }
              String key1 = key.toString();
              key = new Text(key1);
              //context.write会默认在key,value中间添加tab键
              context.write(key,new IntWritable(count));
        }
    }


    public static void main(String[] args) throws Exception{
        //HADOOP_HOME为空，需要配置hadoop的位置，Could not locate executable null\bin\winutils.exe in the Hadoop binaries
        System.setProperty("hadoop.home.dir","D:/programingSoftware/IDEA/Big_Data/hadoop-2.6.5");

        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

        //设置缓存文件到map task的指定工作目录
        job.addCacheFile(new URI("file:/c:/MapJoinTest/product.txt"));

        //指定本程序的jar包所在的本地路径
        job.setJarByClass(InverseIndexFirst.class);

        //指定本业务job要使用的mapper/Reducer业务类
        job.setMapperClass(InverseIndexMapper.class);
        job.setReducerClass(InverseIndexReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        //指定最终输出的数据的kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        //注意启动main函数需要输入参数，否则会出现数组越界异常
        FileInputFormat.setInputPaths(job,new Path("D:/mapreduceTest/input1/"));
        FileOutputFormat.setOutputPath(job,new Path("D:/mapreduceTest/output6/"));

        //将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
        boolean b = job.waitForCompletion(true);
        System.exit(b?0:1);
    }
}

