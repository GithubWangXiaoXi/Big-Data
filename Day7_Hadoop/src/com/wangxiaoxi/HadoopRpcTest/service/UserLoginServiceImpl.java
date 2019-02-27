package com.wangxiaoxi.HadoopRpcTest.service;

public class UserLoginServiceImpl implements UserLoginService{

    @Override
    public String login(String username, String password) {
        return "Hello,your username is "+username+",password is "+password;
    }
}
