package com.wangxiaoxi.HadoopRpcTest.client;

import com.wangxiaoxi.HadoopRpcTest.service.UserLoginService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import java.io.IOException;
import java.net.InetSocketAddress;

public class UserLoginAction {

    public static void main(String[] args) throws Exception {
        UserLoginService xiaoxiServer2 = RPC.getProxy(UserLoginService.class, 100L,
                new InetSocketAddress("localhost", 9000), new Configuration());
        String result = xiaoxiServer2.login("wangxiaoxi", "19971010abc");
        System.out.println(result);
    }
}
