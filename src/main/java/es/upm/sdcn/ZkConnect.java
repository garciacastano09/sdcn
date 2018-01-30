package es.upm.sdcn;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.CreateMode;

public class ZkConnect {
    static Logger LOG = Logger.getLogger("sdcn");

    private ZooKeeper zk;
    private static ZkConnect zkConnect;
    private CountDownLatch connSignal = new CountDownLatch(0);
    private Collection<Integer> clientsANCache = new ArrayList<>();

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
//        zk.getData(path,new SDCNWatcher(zkConnect),zk.exists(path,false));
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


    public ZooKeeper getZookeeper(){return zk;}


    public boolean updateClientsCache(Collection<Integer> clientAN){
        LOG.log(Level.INFO, "updateClientsCache called");

        ClientService clientService = new ClientService();
        PostgreSQLClient pgClient = new PostgreSQLClient();
        LOG.log(Level.INFO, "updateClientsCache - colletion passed: "+clientAN.toString());

        if (clientAN.size() > this.clientsANCache.size()){
//          Han sido creados nuevos nodos
            ArrayList<Integer> newCache = cloneIntegerArrayList(clientAN);

            newCache.removeAll(this.clientsANCache);
            LOG.log(Level.INFO, "updateClientsCache - new nodes have been created: "+newCache.toString());

            Iterator<Integer> iterNewCache = newCache.iterator();

            while (iterNewCache.hasNext()) {
                int cAN = iterNewCache.next();
                Client newClient = clientService.getClientFromZK(cAN);
                LOG.log(Level.INFO, "updateClientsCache - Client "+cAN+" got from ZK");
                if(!pgClient.createClient(newClient)){
                    LOG.log(Level.INFO, "updateClientsCache - Could not post "+cAN+" to PG");
                    return false;
                }
                LOG.log(Level.INFO, "updateClientsCache - Client "+cAN+" posted in PG. Saving in cache.");
            }
            this.clientsANCache = clientAN;
        }
        else if (clientAN.size() < this.clientsANCache.size()){
//          Han sido borrados algunos nodos
            ArrayList<Integer> oldCache = cloneIntegerArrayList(this.clientsANCache);

            oldCache.removeAll(clientAN);
            LOG.log(Level.INFO, "updateClientsCache - some nodes have been deleted: "+oldCache.toString());

            Iterator<Integer> iterOldCache = oldCache.iterator();

            while (iterOldCache.hasNext()) {
                int cAN = iterOldCache.next();
                if(!pgClient.deleteClient(cAN)){
                    LOG.log(Level.INFO, "updateClientsCache - Could not delete "+cAN+" from PG");
                    return false;
                }
                LOG.log(Level.INFO, "updateClientsCache - Client "+cAN+" deleted from PG. Saving in cache.");
            }
            this.clientsANCache = clientAN;
        }
        LOG.log(Level.INFO, "updateClientsCache - final cache: "+this.clientsANCache.toString());
//      Listas iguales
        return true;
    }

    private ArrayList<Integer> cloneIntegerArrayList (Collection<Integer> src){
        ArrayList<Integer> result = new ArrayList<>();
        result.addAll(src);
        return result;
    }

}
