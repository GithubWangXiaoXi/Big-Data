package com.wangxiaoxi.HadoopRpcTest.server;

import com.wangxiaoxi.HadoopRpcTest.service.ClientNameNodeProtocol;
import com.wangxiaoxi.HadoopRpcTest.service.MyNameNode;
import com.wangxiaoxi.HadoopRpcTest.service.UserLoginService;
import com.wangxiaoxi.HadoopRpcTest.service.UserLoginServiceImpl;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.Server;

import java.io.IOException;

public class PublishServiceUtil {

    public static void main(String[] args) throws IOException {
        /**
         *      通过传入hadoop的默认配置，创建builder，并设置ip，端口，协议和实体对象，之前netty的rpc
         *  是spring来实现的，将接口和实体类保存在hashmap中，这里用builder来封装启动接口和实体类
         *      待会client通过动态代理对象，将接口和方法名封装在request，发给server去获得对应的name node
         * （service）的提供的方法
         */
        RPC.Builder builder = new RPC.Builder(new Configuration());
        builder.setBindAddress("localhost")
                .setPort(8888)
                .setProtocol(ClientNameNodeProtocol.class)
                .setInstance(new MyNameNode());
//              .setInstance(MyNameNode.class);

        Server server = builder.build();
        server.start();


        RPC.Builder builder1 = new RPC.Builder(new Configuration());
        builder1.setBindAddress("localhost")
                .setPort(9000)
                .setProtocol(UserLoginService.class)
                .setInstance(new UserLoginServiceImpl());
//              .setInstance(MyNameNode.class);

        Server server1 = builder1.build();
        server1.start();
    }
}
