package wangxiaoxi.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SimpleZkTest {

    private final String stringConnecting = "192.168.133.132:2181";
    private final int sessionTimeout = 2000;
    private ZooKeeper zooKeeper;

    @Before
    //建立zookeeper连接（需要主机+端口，还要配置监听器，这样get）
    public void getConnect() throws Exception{
        zooKeeper = new ZooKeeper(stringConnecting, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println(watchedEvent.getType()+" and "+watchedEvent.getPath());
            }
        });
    }

    @Test
    public void createNode() throws Exception{
        String s = zooKeeper.create("/servers", "Hello World".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println(s);
        Thread.sleep(Long.MAX_VALUE);
    }

    @Test
    public void setNode() throws Exception{
        zooKeeper.setData("/app1","Hello Big Data".getBytes(),-1);
        byte[] data = zooKeeper.getData("/app1", false, null);
        System.out.println(new String(data));
    }

    @Test
    public void deleteNode() throws Exception{
       zooKeeper.delete("/servers",-1);
    }

    @Test
    public void getChildren() throws Exception{
        List<String> children = zooKeeper.getChildren("/", true);
        List<String> servers = new ArrayList<>();
        for(String child:children){
            byte[] data = zooKeeper.getData("/"+child,false,null);
            String data1 = new String(data);
            servers.add(data1);
        }
        System.out.println(servers);
    }

    @Test
    public void isExist() throws Exception{
       Stat status = zooKeeper.exists("/servers",null);
       System.out.println(status);
       System.out.println(status != null ? "exist":"not exist");
    }

    @Test
    public void getData() throws Exception{
        byte[] data = zooKeeper.getData("/app1",true,null);
        System.out.println(new String(data));
        Thread.sleep(Long.MAX_VALUE);
    }


    @Test
    public void test1(){

        //建立zk连接
        //增删改查

    }
}
