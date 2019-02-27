package com.wangxiaoxi.HadoopRpcTest.service;

public interface UserLoginService {
    final static long versionID = 100L;
    String login(String username,String password);
}
