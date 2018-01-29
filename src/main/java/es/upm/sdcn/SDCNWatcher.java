package es.upm.sdcn;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import java.util.logging.Logger;

import static es.upm.sdcn.Serializer.fromByteToObject;

public class SDCNWatcher implements Watcher, AsyncCallback.StatCallback {

    public ZkConnect zk;
    Logger LOG = Logger.getLogger("sdcn");

    public SDCNWatcher(ZkConnect zk){
        this.zk=zk;
    }

    @Override
    public void processResult(int i, String s, Object o, Stat stat) {
    }

    @Override
    public void process(WatchedEvent watchedEvent) {

        //TIPO DE EVENTO QUE MANDA ZK y PATH DEL NODO SOBRE EL QUE SE HA DADO LA OCURRENCIA DEL EVENTO

        String path = watchedEvent.getPath();
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

        if(type.equals(Event.EventType.NodeChildrenChanged)){

        }
        /*switch(type){

            //CREATE
            case NodeCreated:

                    if(new PostgreSQLClient().createClient(Response_client)){
                        LOG.log(Level.INFO, "Nuevo registro en BBDD");
                    }
                    else{
                        LOG.log(Level.INFO, "ERROR");
                    }

                break;

            //UPDATE
            case NodeDataChanged:
                    if(new PostgreSQLClient().updateClient(Response_client.getAccountNumber(),Response_client.getBalance())){
                        LOG.log(Level.INFO, "Registro actualizado");
                    }
                    else{
                        LOG.log(Level.INFO, "ERROR");
                    }

                break;

            //DELETE
            case NodeDeleted:
                    if(new PostgreSQLClient().deleteClient(Response_client.getAccountNumber())){
                        LOG.log(Level.INFO, "Registro eliminado");
                    }
                    else{
                        LOG.log(Level.INFO, "ERROR");
                    }

                break;
            default: LOG.log(Level.INFO, "TIPO:"+type+" PATH:"+path); break;
        }*/

        //SALIR DE LA BARRERA
        /**try {
            barrier.leave();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        //LEVANTAR UN NUEVO WATCHER
        SDCNWatcher w = new SDCNWatcher(zk);

        try {
            zk.getChildren("/clients",w);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
