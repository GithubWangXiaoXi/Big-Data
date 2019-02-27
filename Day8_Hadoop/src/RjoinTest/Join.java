package RjoinTest;

import mapJoinTest.InfoBean;
import org.apache.commons.beanutils.BeanUtils;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Join {


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

    static class PIDjoinMapper extends Mapper<LongWritable,Text,Text, InfoBean>{

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            value = transformTextToUTF8(value,new String("GBK"));

            String content = value.toString();
            String[] tokens = content.split(",");

            String product_id="";
            InfoBean infoBean = new InfoBean();

            //由于输入的文件有product和order文件，所以需要先获取输入文件的名字
            FileSplit split = (FileSplit) context.getInputSplit();
            String name = split.getPath().getName();
            if(name.startsWith("order")){
                /**
                 *  order表的数据：
                    1001,20180827,P0001,2
                    1002,20180827,P0001,3
                 */
                  product_id = tokens[2];
                  infoBean.set(tokens[0],tokens[1],product_id,Integer.parseInt(tokens[3]),
                          "","",0.0,"0");

            }else{
                /**
                 *  product表的数据：
                 *  P0001,魅族pro,C01,1500
                    P0002,华为nova,C02,2000
                    P0003,小米8,C03,2900
                 */
                product_id = tokens[0];
                infoBean.set("0","0",product_id,0,tokens[1],tokens[2],
                        Double.parseDouble(tokens[3]),"1");
            }
            System.out.println(infoBean);
            context.write(new Text(product_id),infoBean);
        }
    }

    //reducer可以通过product_id(key值)，将两个表（及不完全的infoBean对象）join在一起
    //reduce方法处理同一个product_id的不完全的infoBean对象，使orderBean内容完整并输出
    static class PIDjoinReducer extends Reducer<Text,InfoBean,InfoBean,NullWritable>{

        @Override
        protected void reduce(Text key, Iterable<InfoBean> values, Context context) throws IOException, InterruptedException {
            //注意orderBean可以有多个，而productBean只有一个(同一个商品的不同订单），输出的是orderBean的扩展版InfoBean
            InfoBean productBean = new InfoBean();
            ArrayList<InfoBean> orderBeans = new ArrayList<>();

            System.out.println("----------------------------------------------------------------");
            for(InfoBean infoBean : values){

                System.out.println(infoBean);
                //如果是orderBean，则先用反射将values中的orderBean添加到list中。

                if("0".equals(infoBean.getFlag())){
                    InfoBean orderBean = new InfoBean();
                    try {
                        BeanUtils.copyProperties(orderBean,infoBean);
                        orderBeans.add(orderBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        BeanUtils.copyProperties(productBean,infoBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            // 再将productBean中的信息拷贝到orderBean中,这时需要全部的orderBean都添加到orderBeans中
            for(InfoBean infoBean1:orderBeans){
                infoBean1.setProduct_name(productBean.getProduct_name());
                infoBean1.setCategory_id(productBean.getCategory_id());
                infoBean1.setPrice(productBean.getPrice());

                context.write(infoBean1,NullWritable.get());
            }
        }
    }

    public static void main(String[] args) throws Exception{
        //HADOOP_HOME为空，需要配置hadoop的位置，Could not locate executable null\bin\winutils.exe in the Hadoop binaries
        System.setProperty("hadoop.home.dir","D:/programingSoftware/IDEA/Big_Data/hadoop-2.6.5");

        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

        //指定本程序的jar包所在的本地路径
        job.setJarByClass(Join.class);

        //指定本业务job要使用的mapper/Reducer业务类
        job.setMapperClass(PIDjoinMapper.class);
        job.setReducerClass(PIDjoinReducer.class);

        //指定mapper输出数据的kv类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(InfoBean.class);

        //指定最终输出的数据的kv类型
        job.setOutputKeyClass(InfoBean.class);
        job.setOutputValueClass(NullWritable.class);

        //注意启动main函数需要输入参数，否则会出现数组越界异常
        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        //将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
        boolean b = job.waitForCompletion(true);
        System.exit(b?0:1);
    }
}
