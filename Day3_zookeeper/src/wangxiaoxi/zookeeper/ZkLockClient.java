package wangxiaoxi.zookeeper;

import org.apache.zookeeper.*;

import java.util.*;

public class ZkLockClient {

    private final String stringConnecting = "xiaoxi_server1:2181";
    private final int sessionTimeout = 2000;
    private ZooKeeper zooKeeper = null;
    private final String parentLock = "/locks";
    private volatile List<String> locks;
    //该程序每次创建的lock节点的路径
    private String createdlock;
    //该程序的主机名
    private static String hostname = null;

    public void getConnect() throws Exception{
        zooKeeper = new ZooKeeper(stringConnecting, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                //监听器获取子节点，并排序,选出最小序号的程序获取资源（会释放锁），需要再次创建锁

                //    这里存在一个问题，如果多个程序的多个监听器同时都对locks的子节点进行排序，会造成有些资源
                // 已经在使用了，监听器却不知道，继续排序，让最小序号的锁中的数据（主机名）的客户端继续获取资源，这样
                // 就造成了冲突。如果其他主机不能控制别的主机按最小序号来运行，则可以避免一台主机被多台主机“唤醒”

                //    应该不用考虑这个，如果监听器判断zk中最小序号的数据是否是自己主机的名字，如果是，则运行，不是则等待，
                //则可以避免多个监听器同时执行造成的冲突！！
                try {
                        getChildren();
                        if(locks.size() > 1){
                            Thread.sleep(2000);
                            Collections.sort(locks);
                            String firstLock = locks.get(0);
                            System.out.println("第一个锁是:"+firstLock);

                            boolean status = isMyHostname(firstLock);
                            //如果是本主机名的话，获取资源，释放资源，并重新注册锁
                            if(status){
                                doBussiness(firstLock);
                                createLock();
                            }
                        }
//                        else if(locks.size() == 1){
//                            System.out.println("目前只有一个锁是:"+locks);
//                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //判断最小节点锁中的主机数据是不是本机
    public boolean isMyHostname(String firstLock) throws Exception{
        byte[] data = zooKeeper.getData(parentLock + "/" + firstLock, false, null);
        if(new String(data).equals(hostname)){
            return true;
        }else{
            return false;
        }
    }

    //获得资源，资源使用结束后，释放锁
    public void doBussiness(String firstLock) throws Exception{
        System.out.println(hostname+"获得资源");
        System.out.println(hostname+"释放资源");
        zooKeeper.delete(parentLock+"/"+firstLock,-1);
    }

    public void createParentLock() throws Exception{
        //判断父节点是否存在
        if(zooKeeper.exists(parentLock,false) == null){
            zooKeeper.create(parentLock, "locks".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    public void createLock() throws Exception{
        createdlock = zooKeeper.create(parentLock+"/"+"lock",hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    //获取子节点的锁
    public void getChildren() throws Exception{
        //获取子节点的名字（而不是子节点中的数据）
        List<String> children = zooKeeper.getChildren(parentLock, true);
        locks = children;
        if(locks.size() != 0){
            System.out.println("目前各客户端已经注册的锁:"+locks);
        }else{
            System.out.println("目前各客户端没有注册锁");
        }
    }

    public static void main(String[] args) throws Exception{

        //先建立zk连接
        ZkLockClient zkLockClient = new ZkLockClient();
        zkLockClient.getConnect();
        zkLockClient.createParentLock();

        //先注册lock到zk中（当有2个lock节点时，才会到监听器中选取最小的）
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入hostname:");
        hostname = scanner.nextLine();
        zkLockClient.createLock();

        Thread.sleep(Long.MAX_VALUE);
    }

}
