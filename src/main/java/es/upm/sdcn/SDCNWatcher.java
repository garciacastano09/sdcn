package es.upm.sdcn;


import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import java.util.ArrayList;
import org.apache.zookeeper.Watcher;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static es.upm.sdcn.Serializer.fromByteToObject;

public class SDCNWatcher implements Watcher, AsyncCallback.StatCallback {

    public ZkConnect zk;
    Logger LOG = Logger.getLogger("sdcn");


    public SDCNWatcher(ZkConnect zk) {
        this.zk = zk;
    }


    @Override
    public void processResult(int i, String s, Object o, Stat stat) {

    }

    @Override
    public void process(WatchedEvent watchedEvent) {

        //TIPO DE EVENTO QUE MANDA ZK y PATH DEL NODO SOBRE EL QUE SE HA DADO LA OCURRENCIA DEL EVENTO
        LOG.log(Level.INFO, "Watcher activado: "+watchedEvent.toString());

        String path = watchedEvent.getPath();
        LOG.log(Level.INFO, "Path watched: "+path);

        Event.EventType type = watchedEvent.getType();
        Client Response_client = null;

        //DATOS DEL NODO
        try {
            Response_client = (Client) fromByteToObject(zk.getNode(path));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //A LA LLEGADA DE UN EVENTO (WATCHER LO DETECTA) SE ENTRA EN UNA BARRERA (ver ServerBarrier)

        /**ServerBarrier barrier = new ServerBarrier("zk1:2181,zk2:2181,zk3:2181",z,"/barrier",1);
         try{
         boolean flag = barrier.enter();
         System.out.println("Entered barrier: " + 4);
         if(!flag) LOG.log(Level.INFO, "ENTRA EN BARRERA");
         } catch (KeeperException e){

         } catch (InterruptedException e){

         }*/
        LOG.log(Level.INFO, "Evento tipo: "+type);

        if (type.equals(Event.EventType.NodeChildrenChanged)) {

            //COGER PATH
            Collection<Integer> paths = null;
            try {
                paths = getPathList(path);
                if(zk.updateClientsCache(paths)){
                    LOG.log(Level.INFO, "Registro creado");
                }
                zk.getChildren("/clients", this);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (type.equals(Event.EventType.NodeDataChanged)) {

            LOG.log(Level.INFO, "TIPO:" + type + " PATH:" + path);

            if (new PostgreSQLClient().updateClient(Response_client.getAccountNumber(), Response_client.getBalance())) {
                LOG.log(Level.INFO, "Registro actualizado");
            } else {
                LOG.log(Level.INFO, "ERROR");
            }

            ZooKeeper z = zk.getZookeeper();

            try {
                z.getData(path, this, z.exists(path, false));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //SALIR DE LA BARRERA
        /**try {
         barrier.leave();
         } catch (KeeperException e) {
         e.printStackTrace();
         } catch (InterruptedException e) {
         e.printStackTrace();
         }*/

    }

    private Collection<Integer> getPathList(String path) throws Exception {

        ZooKeeper z = zk.getZookeeper();
        List<String> nodos = z.getChildren(path,false);
        List<Integer> intList = new ArrayList<>();
        for(String s : nodos) intList.add(Integer.valueOf(s));

        return intList;
    }


}
