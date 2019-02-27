package wangxiaoxi.zookeeper;

import org.apache.zookeeper.*;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

public class ZkServer {

    private final String stringConnecting = "xiaoxi_server1:2181";
    private final int sessionTimeout = 2000;
    private ZooKeeper zooKeeperServer;
    private final String parentNode = "/servers";


    //建立zookeeper连接（需要主机+端口，还要配置监听器，这样get）
    public void getConnect() throws Exception{
        zooKeeperServer = new ZooKeeper(stringConnecting, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println(watchedEvent.getType()+" and "+watchedEvent.getPath());
            }
        });
    }

    public void createParentNode() throws Exception{
        //判断父节点是否存在
        if(zooKeeperServer.exists(parentNode,false) == null){
            zooKeeperServer.create(parentNode, "serversList".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    public void createServerNode(List<String> serverList) throws Exception {

        for(String hostname:serverList){
            String serverNode = zooKeeperServer.create(parentNode + "/" + hostname, hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println(serverNode);
        }
    }


    public static void main(String[] args) throws Exception {
        //先连接zookeeper
        ZkServer zkServer = new ZkServer();
        zkServer.getConnect();

        //再往zookeeper中写入服务器信息
        zkServer.createParentNode();
        List<String> serverList = new ArrayList<>();
        serverList.add("xiaoxi_server1");
        serverList.add("xiaoxi_server2");
        serverList.add("xiaoxi_server3");
        zkServer.createServerNode(serverList);

        //业务功能
        Thread.sleep(Long.MAX_VALUE);
    }
}
