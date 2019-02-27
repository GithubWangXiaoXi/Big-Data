package com.wangxiaoxi.hadoop.hdfs;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;


public class HadoopHdfDemo {

    FileSystem fs = null;
    Configuration configuration = null;
    @Before
    public void init() throws Exception {
        configuration = new Configuration();
//        configuration.set("fs.defaultFS","hdfs://xiaoxiServer2:9000");

        fs = FileSystem.get(new URI("hdfs://xiaoxiServer2:9000"),configuration,"root");
    }

    @Test
    public void uploadFile() throws Exception{
        fs.copyFromLocalFile(new Path("D:/in.txt"),new Path("/in.copy.txt"));
        fs.close();
    }

    @Test
    public void downloadFile() throws Exception{
        /**
            java.io.IOException: (null) entry in command string: null chmod 0644 D:。。。
            解决方法:将fs.copyToLocalFile( hdfsPath,localPath);改为fs.copyToLocalFile( false,hdfsPath,
         localPath,true);
            原因:不理解,但加上这两个后确实可以下载
         */

        //false指不删除hdfs中的文件
        fs.copyToLocalFile(false,new Path("/1.txt.copy"),
                new Path("D:/1.txt.copy"),true);
        fs.close();
    }

    @Test
    public void getConf(){
        System.out.println(configuration.toString());
        //数据过多，不能放在集合中，所以用迭代器来将数据打印出来
        Iterator<Map.Entry<String, String>> iterator = configuration.iterator();
        while(iterator.hasNext()){
            Map.Entry<String, String> next = iterator.next();
            System.out.println(next.getKey()+":"+next.getValue());
        }
    }

    @Test
    public void makeDir() throws Exception {
        boolean flag = fs.mkdirs(new Path("/mkdirTest/aaa/bbb"));
        System.out.println(flag);
        fs.close();
    }

    @Test
    public void deleteDir() throws Exception {
        boolean flag = fs.delete(new Path("/mkdirTest"),true);
        System.out.println(flag);
        fs.close();
    }

    @Test
    public void testLsFile() throws Exception{
        RemoteIterator<LocatedFileStatus> remoteIterator = fs.listFiles(new Path("/"), true);
        while(remoteIterator.hasNext()){
            LocatedFileStatus next = remoteIterator.next();
            System.out.print("Permission:"+next.getPermission());
            System.out.print("    Owner:"+next.getOwner());
            System.out.print("    Group:"+next.getGroup());
            System.out.print("    Replication:"+next.getReplication());
            System.out.print("    BlockSize:"+next.getBlockSize());
            System.out.println("    name:"+next.getPath().getName());
            BlockLocation[] blockLocations = next.getBlockLocations();
            for(BlockLocation blockLocation : blockLocations){
                System.out.println("block的长度："+blockLocation.getLength());

                String[] hosts = blockLocation.getHosts();
                for(String host:hosts){
                    System.out.println("该block储存在dataNode："+host);
                }

                System.out.println("block的偏移量："+blockLocation.getOffset());
            }
            System.out.println("-----------------------------------------");
        }
    }

    @Test
    public void testLsStatus() throws Exception{
        FileStatus[] fileStatuses = fs.listStatus(new Path("/"));
        for(FileStatus fileStatus : fileStatuses){
            System.out.print(fileStatus.getPath().getName()+":      ");
            System.out.println(fileStatus.isFile()?"File":"Directory");
        }
    }

    @Test
    public void testUpload() throws Exception{
        FSDataOutputStream fsDataOutputStream = fs.create(new Path("/aaa.sql"));
        FileInputStream fileInputStream = new FileInputStream("C:/aaa.sql");
        IOUtils.copy(fileInputStream,fsDataOutputStream);
    }

    @Test
    public void testDownload() throws Exception{
        FSDataInputStream fsDataInputStream = fs.open(new Path("/aaa.sql"));
        IOUtils.copy(fsDataInputStream,System.out);
    }
}
