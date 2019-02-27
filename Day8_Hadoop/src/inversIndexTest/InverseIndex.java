package inversIndexTest;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.*;
import java.net.URI;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class InverseIndex {

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


    static class InverseIndexMapper extends Mapper<LongWritable,Text,Text,Text> {

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            //获取切片名称
            FileSplit split = (FileSplit) context.getInputSplit();
            String fileName = split.getPath().getName();

            String content = value.toString();

            String[] words = content.split(" ");

            //key:word value:fileName
            for(String word:words) {
                context.write(new Text(word),new Text(fileName));
            }
        }
    }

    static class InverseIndexReducer extends Reducer<Text,Text,Text,Text> {

        Map<String,Boolean> filenamesMap = new HashMap();
        /**
         *     根据文件数量来设置统计变量的个数,并且根据求余来获取统计文件对应数组下标
           numForFileNames在统计新的单词时需要初始化为0。但是用setup方法初始化，会导致第二个单词在
           统计时数组的值不为0，导致单词统计错误
         */
        Integer[] numForFileNames = new Integer[100];

//        @Override
//        protected void setup(Context context) throws IOException, InterruptedException {
//            for(int i=0; i<100; i++) {
//                numForFileNames[i] = 0;
//            }
//        }

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

            //在新单词统计前进行数组的置0
            for(int i=0; i<100; i++) {
                numForFileNames[i] = 0;
            }

            List<String> filenameList = new ArrayList<>();

            //map传入的数据是key:word value:fileName，需要统计同一个单词的同一个fileName的个数(注意文件名是有带后缀的)
             for(Text filename:values) {
                 String filename1 = filename.toString();
                 filenamesMap.put(filename1.substring(0,filename1.length()-4),true);

                 //由于mapreduce的迭代器只能使用一次，用list集合来存储迭代器中的值
                 filenameList.add(filename1);
             }

              //迭代器的指针已经移动到最后一位了，迭代器不能再次遍历了
//             for(Text filename:values) {
//                 //问：所有的字符串都能转换成数字吗
//                 String filename1 = filename.toString();
//                 int index = Integer.parseInt(filename1.substring(0,filename1.length()-4)) % filenamesMap.size();
//                 Integer count = numForFileNames[index];
//                 System.out.println(count);
//                 numForFileNames[index] = count+1;
//             }

            //用list集合来遍历filename
             for(String filename:filenameList){
                 int index = Integer.parseInt(filename.substring(0,filename.length()-4)) % filenamesMap.size();
                 Integer count = numForFileNames[index];
                 //如果为对应的count值空的话，则置为1
                 if(count == 0){
                     numForFileNames[index] = 1;
                     count = numForFileNames[index];
                 }else{
                     numForFileNames[index] = count+1;
                 }
                 System.out.println(count);
             }

             //拼接各文件单词统计的字符串（要求文件名字符串必须是可以转换成整数的）
            Set<String> keySet = filenamesMap.keySet();
            StringBuffer stringBuffer = new StringBuffer();
            String perFileCount = "";
            for(String filename:keySet) {
                 int index = Integer.parseInt(filename) % filenamesMap.size();
                 perFileCount = filename+".txt-->"+numForFileNames[index]+"   ";
                 stringBuffer.append(perFileCount);
            }

            stringBuffer.append("\n");
            context.write(key,new Text(stringBuffer.toString()));
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
        job.setJarByClass(InverseIndex.class);

        //指定本业务job要使用的mapper/Reducer业务类
        job.setMapperClass(InverseIndexMapper.class);
        job.setReducerClass(InverseIndexReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        //指定最终输出的数据的kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        //注意启动main函数需要输入参数，否则会出现数组越界异常
        FileInputFormat.setInputPaths(job,new Path("D:/mapreduceTest/input1/"));
        FileOutputFormat.setOutputPath(job,new Path("D:/mapreduceTest/output5/"));

        //将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
        boolean b = job.waitForCompletion(true);
        System.exit(b?0:1);
    }
}

