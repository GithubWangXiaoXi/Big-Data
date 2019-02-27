package sharefriendTest;

import RjoinTest.Join;
import mapJoinTest.InfoBean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class ShareFriendsStepOne {

    /**
     * 获取用户之间的共同好友，eg  person:friends  a:b,c,d,e;   b:a,d,e;   end: a-b:d,e
     * 首先：遍历得到每一个firend对应的person
     * 其次：将person进行排序，两两配对，统计person之间的共同好友friends
     *
     * A:B,C,D,F,E,O
     B:A,C,E,K
     C:F,A,D,I
     */
    //map输出每一个friend对应的person。在reduce中可以统计friend的所有person
    static class ShareFriendsStepOneMapper extends Mapper<LongWritable,Text,Text,Text>{
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String content = value.toString();
            String[] person_friends = content.split(":");

            String person = person_friends[0];
            String[] friends = person_friends[1].split(",");

            for(String friend : friends){
                context.write(new Text(friend),new Text(person));
            }
        }
    }

    //在reduce中可以统计friend的所有person
    static class ShareFriendsStepOneReducer extends Reducer<Text,Text,Text,Text>{
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            StringBuffer persons = new StringBuffer();
            for(Text person:values){
                persons.append(person).append(",");
            }
            context.write(key,new Text(persons.toString()));
        }
    }

    public static void main(String[] args) throws Exception{
        //HADOOP_HOME为空，需要配置hadoop的位置，Could not locate executable null\bin\winutils.exe in the Hadoop binaries
        System.setProperty("hadoop.home.dir","D:/programingSoftware/IDEA/Big_Data/hadoop-2.6.5");

        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

        //指定本程序的jar包所在的本地路径
        job.setJarByClass(ShareFriendsStepOne.class);

        //指定本业务job要使用的mapper/Reducer业务类
        job.setMapperClass(ShareFriendsStepOneMapper.class);
        job.setReducerClass(ShareFriendsStepOneReducer.class);

        //指定mapper输出数据的kv类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        //指定最终输出的数据的kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        //注意启动main函数需要输入参数，否则会出现数组越界异常
        FileInputFormat.setInputPaths(job,new Path("D:/mapreduceTest/personFriendsText/input"));
        FileOutputFormat.setOutputPath(job,new Path("D:/mapreduceTest/personFriendsText/output"));

        //将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
        boolean b = job.waitForCompletion(true);
        System.exit(b?0:1);
    }
}

