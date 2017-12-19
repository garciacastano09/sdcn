package es.upm.sdcn;

import com.google.gson.Gson;

import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.zookeeper.ZooKeeper;

import static es.upm.sdcn.Serializer.fromObjectToByte;


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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(CLIENT_ZK_PATH_PREFIX);
        stringBuilder.append(client.getAccountNumber());

        try{
            LOG.log(Level.INFO, "Posting into ZK");
            this.zkConnect.createNode(stringBuilder.toString(), fromObjectToByte(client));
        }
        catch(Exception e){
            LOG.log(Level.SEVERE, "Error posting into ZK");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        LOG.log(Level.INFO, "OK Post into ZK");
        return Response.status(Response.Status.OK).build();
    }

    public Response updateClient(Client client){
        LOG.log(Level.INFO, "ClientService.updateClient called");
        Gson gson = new Gson();


        return Response.status(Response.Status.OK).entity((gson.toJson(client))).build();
    }


    public Response getClient(int accountNumber){
        LOG.log(Level.INFO, "ClientService.getClient(accountNumber) called");

        Gson gson = new Gson();

        return Response.status(Response.Status.OK).build();
    }

    public Response deleteClient(int accountNumber){
        LOG.log(Level.INFO, "ClientService.deleteClient called");
        Gson gson = new Gson();

        String result = gson.toJson("{accountNumber:"+accountNumber+"}");
        return Response.status(Response.Status.OK).entity(result).build();
    }

//    public Response getClient(String clientName){
//        LOG.log(Level.INFO, "ClientService.getClient(clientName) called");
//        Gson gson = new Gson();
//        PostgreSQLClient cdb = new PostgreSQLClient();
//        Client client = cdb.readClient(clientName);
//
//        return Response.status(Response.Status.OK).entity((gson.toJson(client))).build();
//    }


}
