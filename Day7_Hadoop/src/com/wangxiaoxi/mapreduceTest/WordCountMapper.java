package com.wangxiaoxi.mapreduceTest;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * 单词统计：输入的key:起始偏移量  Long   LongWritable
 *          输入的value:一行文本内容 String  Text
 *          输出的key:单词  String  Text
 *          输出的value:次数（一般为1，迭代统计由reduce管） Integer  IntWritable
 */
public class WordCountMapper extends Mapper<LongWritable,Text ,Text,IntWritable>{

    /**
     *   map task每次读取一行文本内容，会执行map方法。
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        /**
         * 得到一行文本内容，并对其进行分词处理,并依次将单词写出
         */
        String content = value.toString();
        String words[] = content.split(" ");

        for(String word:words){
            context.write(new Text(word),new IntWritable(1));
        }
    }
}
