package es.upm.sdcn;

import com.google.gson.Gson;

import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ClientService {

    Logger LOG = Logger.getLogger("sdcn");

    public ClientService(){}

    public Response createClient(Client client){
        LOG.log(Level.INFO, "ClientService.createClient called");
        Gson gson = new Gson();
        PostgreSQLClient cdb = new PostgreSQLClient();
        if(!cdb.createClient(client)){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.status(Response.Status.OK).entity((gson.toJson(client))).build();
    }

    public Response updateClient(Client client){
        LOG.log(Level.INFO, "ClientService.updateClient called");
        Gson gson = new Gson();
        PostgreSQLClient cdb = new PostgreSQLClient();
        if(!cdb.updateClient(client.getAccountNumber(), client.getBalance())){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(Response.Status.OK).entity((gson.toJson(client))).build();
    }


    public Response getClient(int accountNumber){
        LOG.log(Level.INFO, "ClientService.getClient(accountNumber) called");
        Gson gson = new Gson();
        PostgreSQLClient cdb = new PostgreSQLClient();
        Client client = cdb.readClient(accountNumber);
        if (client==null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(gson.toJson(client)).build();
    }

    public Response deleteClient(int accountNumber){
        LOG.log(Level.INFO, "ClientService.deleteClient called");
        Gson gson = new Gson();
        PostgreSQLClient cdb = new PostgreSQLClient();
        if(!cdb.deleteClient(accountNumber)){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
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
