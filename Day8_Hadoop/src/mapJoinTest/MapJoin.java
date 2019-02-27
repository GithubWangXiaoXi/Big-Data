package mapJoinTest;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.*;
import java.net.URI;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapJoin {

    /**
     *  之前是通过mapreduce实现两张表的join，但是会导致某个产品的订单特别多，数据会倾斜
     *如果将product表加载到map task的工作目录中，让order表拼接product表中的信息，可以解决数据倾斜的问题
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


    static class MapJoinMapper extends Mapper<LongWritable,Text,Text,NullWritable> {

        Map<String,InfoBean> productMap = new HashMap<>();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
             //由于product.txt在map task的工作目录下了，可以直接获取它的数据流
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("product.txt")));
            String line = null;
            //将readLine解码成新的字符串
            while((line = bufferedReader.readLine()) != null){
                //将product.txt中的产品数据封装到InfoBean对象中，并作为map的value
                /**
                 *  private String product_name;
                    private String category_id;
                    private Double price;
                 */
                //将product文件另存为utf8的格式，java即可识别，不会乱码
                System.out.println(line);

//                 line = new String(line.getBytes(),0,line.length(),"GBK");
//                 System.out.println(line);

                 InfoBean infoBean = new InfoBean();

                 String[] field = line.split(",");

                 String product_id = field[0];

                 infoBean.setProduct_name(field[1]);
                 infoBean.setCategory_id(field[2]);
                 infoBean.setPrice(Double.parseDouble(field[3]));

                 productMap.put(product_id,infoBean);
            }
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            //map负责读取每一行order数据
//            value = transformTextToUTF8(value,"GBK");
            String orderInfo = value.toString();
            System.out.println(orderInfo);

            String[] tokens = orderInfo.split(",");
            String product_id = tokens[2];
            String orderCompleted="";

//            Set<String> stringSet = productMap.keySet();
//            for(String key1:stringSet){
//                System.out.println(key1+"->"+productMap.get(key1));
//            }

            InfoBean product = productMap.get(product_id);
            if(product != null){
                orderCompleted = orderInfo + ","+product.getProduct_name()+","+product.getCategory_id()+","
                        +product.getProduct_name()+"\n";
            }

            System.out.println(orderCompleted);

            //只输出order订单的完整信息即可
            context.write(new Text(orderCompleted),NullWritable.get());
        }
    }


    public static void main(String[] args) throws Exception{
        //HADOOP_HOME为空，需要配置hadoop的位置，Could not locate executable null\bin\winutils.exe in the Hadoop binaries
        System.setProperty("hadoop.home.dir","D:/programingSoftware/IDEA/Big_Data/hadoop-2.6.5");

        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

        //设置缓存文件到map task的指定工作目录
        job.addCacheFile(new URI("file:/c:/MapJoinTest/product.txt"));

        //设置reduce task的数目为0
        job.setNumReduceTasks(0);

        //指定本程序的jar包所在的本地路径
        job.setJarByClass(MapJoin.class);

        //指定本业务job要使用的mapper/Reducer业务类
        job.setMapperClass(MapJoinMapper.class);

        //指定最终输出的数据的kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        //注意启动main函数需要输入参数，否则会出现数组越界异常
        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        //将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
        boolean b = job.waitForCompletion(true);
        System.exit(b?0:1);
    }
}
