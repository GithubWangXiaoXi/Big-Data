package sharefriendTest;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.Arrays;

public class comFans {

    /**
     * 求互粉的用户之间有哪些，eg  person:friends  a:b,c,d,e;   b:a,d,e; -> a:b  b:a  comFans:A-B
     * 实现思路：之前已经在第二个map中进行person_person的配对，<person_person,friend>
     *     如果将map的配对方式改成person_friend的配对或friend_person方式(按字符顺序),
     * 则统计配对次数，如果为2，则为互粉用户
     *     但是发现如果map输出的是person_friend的配对，则没必要shareFriendsStepOne,将friends拆分，
     * value为person，这里直接对key,value排序，然后对key_value按字典顺序配对，在reduce中统计次数即可
     */

    //map处理每一条记录：输出每一个person_person对，值为对应相同的friend。
    static class comFansMapper extends Mapper<LongWritable,Text,Text,IntWritable>{
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
           String content = value.toString();
           String[] content1 = content.split(":");

           String person = content1[0];
           String[] friends = content1[1].split(",");

           int persons_friends_size = friends.length+1;
           System.out.println(persons_friends_size);

           //将key，value放入统一个String数组里
           String[] persons_friends = new String[persons_friends_size];
           persons_friends[0] = person;
           for (int i = 1;i<persons_friends_size;i++){
               persons_friends[i] = friends[i-1];
           }

           //需要对key，value组成的persons_friends进行排序，避免出现B-C，C-B的冲突，这样就不能统计互粉的用户了。
           Arrays.sort(persons_friends);
           int personIndex=0;

           //对已经排好序的persons_friends，进行key_value匹配,首先在persons_friends中找到key的位置
           for(int i = 0;i<persons_friends_size;i++){
               if(person.equals(persons_friends[i])){
                   personIndex = i;
                   break;
               }
           }

           //personIndex与之前的friends匹配,friend_person,保证配对串是按字典顺序的
           for(int i=0;i<personIndex;i++){
               String friend_person = persons_friends[i]+"-"+person;
               context.write(new Text(friend_person),new IntWritable(1));
           }

           //personIndex与之后的friends匹配,person_friend
            for(int i=personIndex+1;i<persons_friends_size;i++){
                String person_friend = person+"-"+persons_friends[i];
                context.write(new Text(person_friend),new IntWritable(1));
            }
        }
    }

    //在reduce中可以统计person_person配对的所有friend
    static class comFansReducer extends Reducer<Text,IntWritable,Text,NullWritable>{
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

            int count = 0;
            for(IntWritable a:values){
                count++;
            }
            if(count == 2){
                context.write(key,NullWritable.get());
            }
        }
    }

    public static void main(String[] args) throws Exception{
        //HADOOP_HOME为空，需要配置hadoop的位置，Could not locate executable null\bin\winutils.exe in the Hadoop binaries
        System.setProperty("hadoop.home.dir","D:/programingSoftware/IDEA/Big_Data/hadoop-2.6.5");

        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

        //指定本程序的jar包所在的本地路径
        job.setJarByClass(comFans.class);

        //指定本业务job要使用的mapper/Reducer业务类
        job.setMapperClass(comFansMapper.class);
        job.setReducerClass(comFansReducer.class);

        //指定mapper输出数据的kv类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        //指定最终输出的数据的kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        //注意启动main函数需要输入参数，否则会出现数组越界异常
        FileInputFormat.setInputPaths(job,new Path("D:/mapreduceTest/personFriendsText/input"));
        FileOutputFormat.setOutputPath(job,new Path("D:/mapreduceTest/personFriendsText/output2"));

        //将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
        boolean b = job.waitForCompletion(true);
        System.exit(b?0:1);
    }
}

