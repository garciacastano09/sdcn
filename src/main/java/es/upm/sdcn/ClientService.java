package es.upm.sdcn;

import com.google.gson.Gson;

import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.zookeeper.ZooKeeper;

import static es.upm.sdcn.Serializer.*;


public class ClientService {

    Logger LOG = Logger.getLogger("sdcn");
    private final String CLIENT_ZK_PATH_PREFIX = "/clients/";

    private ZkConnect zkConnect;
    private ZooKeeper zk;


    public ClientService(){
        this.zkConnect = new ZkConnect();
        try{
            this.zk = this.zkConnect.connect("localhost:21811,localhost:21812,localhost:21813");
        }
        catch (Exception e){
            LOG.log(Level.SEVERE, "Could not connect to ZK");
        }

    }

    public Response createClient(Client client){
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
        LOG.log(Level.INFO, "ClientService.getClient(accountNumber) called");
        Client client = null;

        try{
            LOG.log(Level.INFO, "Reading from ZK");
            client = (Client) fromByteToObject(this.zkConnect.getNode(this.getFullZKPath(accountNumber)));
        }
        catch(Exception e){
            LOG.log(Level.SEVERE, "Error reading from ZK");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.OK).entity(new Gson().toJson(client)).build();
    }

    private String getFullZKPath(int accountNumber){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(CLIENT_ZK_PATH_PREFIX);
        stringBuilder.append(accountNumber);
        return stringBuilder.toString();
    }
}
