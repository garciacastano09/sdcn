package es.upm.sdcn;

import com.google.gson.Gson;

import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import static es.upm.sdcn.ApiUtils.fromInputStreamToString;
import static es.upm.sdcn.Serializer.*;


public class ClientService {

    Logger LOG = Logger.getLogger("sdcn");
    public static final String CLIENT_ZK_PATH_PREFIX = "/clients";

    private ZkConnect zkConnect;


    public ClientService(){
        try {
            this.zkConnect = ZkConnect.getZkConnect();
        } catch (Exception e) {
            LOG.log(Level.INFO, "ClientService could not connect with ZK");
            e.printStackTrace();
        }
    }

    public Response createClient(Client client) throws Exception {
        /**
         * TODO Este endpoint escribe directamente en Zookeeper. Falta que, tras ello, se activen watcher que consulten
         * bajo el zkNode /clients. Cuando haya cambios, se deberan reflejar en Postgres con los metodos de
         * PostgreSQLClient
         */
        LOG.log(Level.INFO, "ClientService.createClient called");

        try{
            LOG.log(Level.INFO, "Posting into ZK");
            this.zkConnect.createNode(this.getFullZKPath(client.getAccountNumber()), fromObjectToByte(client));
        }
        catch(Exception e){
            LOG.log(Level.SEVERE, "Error posting into ZK");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        LOG.log(Level.INFO, "OK Post into ZK");

        return Response.status(Response.Status.OK).entity(new Gson().toJson(client)).build();
    }

    public Response updateClient(Client client){
        /**
         * TODO Este endpoint escribe directamente en Zookeeper. Falta que, tras ello, se activen watcher que consulten
         * bajo el zkNode /clients. Cuando haya cambios, se deberan reflejar en Postgres con los metodos de
         * PostgreSQLClient
         */
        LOG.log(Level.INFO, "ClientService.updateClient called");

        try{
            LOG.log(Level.INFO, "Putting into ZK");
            this.zkConnect.updateNode(this.getFullZKPath(client.getAccountNumber()), fromObjectToByte(client));
        }
        catch(Exception e){
            LOG.log(Level.SEVERE, "Error putting into ZK");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        LOG.log(Level.INFO, "OK Put into ZK");
        return Response.status(Response.Status.OK).entity(new Gson().toJson(client)).build();
    }

    public Response deleteClient(int accountNumber){
        /**
         * TODO Este endpoint escribe directamente en Zookeeper. Falta que, tras ello, se activen watcher que consulten
         * bajo el zkNode /clients. Cuando haya cambios, se deberan reflejar en Postgres con los metodos de
         * PostgreSQLClient
         */
        LOG.log(Level.INFO, "ClientService.deleteClient called");

        try{
            LOG.log(Level.INFO, "Deleting from ZK");
            this.zkConnect.deleteNode(this.getFullZKPath(accountNumber));
        }
        catch(Exception e){
            LOG.log(Level.SEVERE, "Error deleting into ZK");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        LOG.log(Level.INFO, "OK Delete from ZK");
        return Response.status(Response.Status.OK).entity(
                new Gson().toJson("{accountNumber:"+accountNumber+"}")).build();
    }

    public Response getClient(int accountNumber){
        /**
         * TODO Este endpoint escribe directamente en Zookeeper. Falta que, tras ello, se activen watcher que consulten
         * bajo el zkNode /clients. Cuando haya cambios, se deberan reflejar en Postgres con los metodos de
         * PostgreSQLClient
         */
        LOG.log(Level.INFO, "RESTResource.getClientPostgres called");
        Client client = new PostgreSQLClient().readClient(accountNumber);
        return Response.status(Response.Status.OK).entity(new Gson().toJson(client)).build();
        /*LOG.log(Level.INFO, "ClientService.getClient(accountNumber) called");
        Client client = null;

        try{
            LOG.log(Level.INFO, "Reading from ZK");
            client = (Client) fromByteToObject(this.zkConnect.getNode(this.getFullZKPath(accountNumber)));
        }
        catch(Exception e){
            LOG.log(Level.SEVERE, "Error reading from ZK");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.OK).entity(new Gson().toJson(client)).build();*/
    }

    private String getFullZKPath(int accountNumber){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(CLIENT_ZK_PATH_PREFIX);
        stringBuilder.append("/");
        stringBuilder.append(accountNumber);
        return stringBuilder.toString();
    }

    public Client getClientFromZK(int accountNumber){
        LOG.log(Level.INFO, "ClientService.getClientFromZK(accountNumber) called");
        Client client = null;
        try{
            LOG.log(Level.INFO, "Reading from ZK");
            client = (Client) fromByteToObject(this.zkConnect.getNode(this.getFullZKPath(accountNumber)));
        }
        catch(Exception e){
            LOG.log(Level.SEVERE, "Error reading from ZK");
            return null;
        }
        return client;
    }
}
