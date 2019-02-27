package wangxiaoxi.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;
import java.util.List;

public class ZkClient {


    private final String stringConnecting = "xiaoxi_server1:2181";
    private final int sessionTimeout = 2000;
    private ZooKeeper zooKeeperClient;
    private final String parentNode = "/servers";
    private volatile List<String> serversList = null;


    //建立zookeeper连接（需要主机+端口，还要配置监听器，这样get）
    public void getConnect() throws Exception{
        zooKeeperClient = new ZooKeeper(stringConnecting, sessionTimeout, new Watcher() {
            @Override
            //监听servers服务器列表，如果监听到子节点有变化，则重新获取服务器列表
            public void process(WatchedEvent watchedEvent) {
                try {
                    getServersList();
                } catch (Exception e) {
                }
            }
        });
    }

    public void getServersList() throws Exception{
        List<String> children = zooKeeperClient.getChildren(parentNode, true);
        List<String> tempList = new ArrayList<>();
        for (String child : children){
            byte[] data = zooKeeperClient.getData(parentNode + "/" + child, false, null);
            tempList.add(new String(data));
        }
        serversList = tempList;
        System.out.println(serversList);
    }


    public static void main(String[] args) throws Exception {
        //先连接zookeeper
        ZkClient zkClient = new ZkClient();
        zkClient.getConnect();

        //再往zookeeper中读出服务器/Servers节点信息
        zkClient.getServersList();

        //业务功能
        Thread.sleep(Long.MAX_VALUE);
    }

}
