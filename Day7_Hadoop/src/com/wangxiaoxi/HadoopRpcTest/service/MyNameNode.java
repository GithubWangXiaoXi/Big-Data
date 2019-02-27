package com.wangxiaoxi.HadoopRpcTest.service;

public class MyNameNode implements ClientNameNodeProtocol{
    @Override
    public String getMetaData(String path) {
        return path +" hello,this is blk_1,blk_2";
    }
}
