package com.wangxiaoxi.mapreduceTest;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

/**
 *  Reducer：输入key是Mapper的输出key，单词  Text
 *           输入value是Mapper的输出的value，次数 IntWritable
 *
 *           输出的key是输出到hdfs的key, 单词 Text
 *           输出的value是输出到hdfs的value 次数 IntWritable
 */
public class WordCountReducer extends Reducer<Text,IntWritable,Text,IntWritable>{

    /**
     * reduce task的工作是将map task的单词统计，并输出到hdfs中
     */
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

        int count = 0;
        Iterator<IntWritable> iterator = values.iterator();
        while(iterator.hasNext()){
           count += iterator.next().get();
        }
        context.write(new Text(key),new IntWritable(count));
    }
}
