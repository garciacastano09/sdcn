package es.upm.sdcn;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.CreateMode;

public class ZkConnect {
    private ZooKeeper zk;
    private static ZkConnect zkConnect;
    private CountDownLatch connSignal = new CountDownLatch(0);
    private List<String> clientsCache = new ArrayList<>();

    public static ZkConnect getZkConnect() throws Exception{
        if(zkConnect == null){
            zkConnect = new ZkConnect("zk1:2181,zk2:2181,zk3:2181");
        }
        return zkConnect;
    }

    private ZkConnect(String connectionString) throws Exception{
        this.connect(connectionString);
//      watcher
    }

    //host should be 127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002
    public ZooKeeper connect(String host) throws Exception {
        zk = new ZooKeeper(host, 3000, new Watcher() {
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    connSignal.countDown();
                }
            }
        });
        connSignal.await();
        return zk;
    }

    public void close() throws InterruptedException {
        zk.close();
    }

    public void createNode(String path, byte[] data) throws Exception
    {
        zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public void updateNode(String path, byte[] data) throws Exception
    {
        zk.setData(path, data, zk.exists(path, true).getVersion());
    }

    public void deleteNode(String path) throws Exception
    {
        zk.delete(path,  zk.exists(path, true).getVersion());
    }

    public byte[] getNode(String path) throws Exception
    {
        return zk.getData(path, true, zk.exists(path, true));
    }

    public List<String> getChildren(String path, Watcher w) throws Exception
    {
        return zk.getChildren(path, w);
    }

    public void updateClientsCache(String clientPath){
        if(clientsCache.contains(clientPath)){
            clientsCache.remove(clientPath);
        }
        else{
            clientsCache.add(clientPath);
        }
    }

}
