package com.wangxiaoxi.HadoopRpcTest.service;

public interface ClientNameNodeProtocol {
    final static long versionID = 1;
    String getMetaData(String path);
}
