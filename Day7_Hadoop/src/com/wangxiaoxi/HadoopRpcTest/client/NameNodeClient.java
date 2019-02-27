package com.wangxiaoxi.HadoopRpcTest.client;

import com.wangxiaoxi.HadoopRpcTest.service.ClientNameNodeProtocol;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.protocolPB.ClientNamenodeProtocolPB;
import org.apache.hadoop.ipc.RPC;
import org.junit.Test;

import java.net.InetSocketAddress;

public class NameNodeClient {

    public static void main(String[] args) throws Exception{
        //客户端通过动态对象来跟服务器端进行RPC通信
        ClientNameNodeProtocol nameNode = RPC.getProxy(ClientNameNodeProtocol.class, 1,
                new InetSocketAddress("localhost", 8888), new Configuration());
        String answer = nameNode.getMetaData("Hello World");
        System.out.println(answer);
    }

//    @Test
//    public void getNameNode() throws Exception{
//        ClientNamenodeProtocolPB xiaoxiServer2 = RPC.getProxy(ClientNamenodeProtocolPB.class, 1,
//                new InetSocketAddress("xiaoxiServer2", 9000), new Configuration());
//        xiaoxiServer2.getBlockLocations()
//
//    }
}
