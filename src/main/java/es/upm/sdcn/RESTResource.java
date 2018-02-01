package es.upm.sdcn;

import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import static es.upm.sdcn.ApiUtils.fromInputStreamToString;

@Path("/")
public class RESTResource{

    Logger LOG = Logger.getLogger("sdcn");

    @Path("client")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createClient(InputStream inputStream) throws Exception {
        LOG.log(Level.INFO, "RESTResource.createClient called");
        String jsonString = fromInputStreamToString(inputStream);
        Gson gson = new Gson();
        Client client = gson.fromJson(jsonString, Client.class);
        return new ClientService().createClient(client);
    }

    @Path("client")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateClient(InputStream inputStream) {
        LOG.log(Level.INFO, "RESTResource.updateClient called");
        String jsonString = fromInputStreamToString(inputStream);
        Gson gson = new Gson();
        Client client = gson.fromJson(jsonString, Client.class);
        return new ClientService().updateClient(client);
    }

    @Path("client")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteClient(@QueryParam("accountNumber") int accountNumber) {
        LOG.log(Level.INFO, "RESTResource.deleteClient called");
        return new ClientService().deleteClient(accountNumber);
    }

    @Path("client")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClient(@QueryParam("accountNumber") int accountNumber) {
        LOG.log(Level.INFO, "RESTResource.getClient(accountNumber) called");
        return new ClientService().getClient(accountNumber);
    }




//--------- DEBUGGING ENDPOINTS ---------
    /**
     *
     * Estos endpoins son solo para debugar que la comunicacion con postgres a traves del servidor, funciona bien
     *
     * */
    @Path("clientPostgres")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createClientPostgres(InputStream inputStream) {
        LOG.log(Level.INFO, "RESTResource.createClient called");
        String jsonString = fromInputStreamToString(inputStream);
        Gson gson = new Gson();
        Client client = gson.fromJson(jsonString, Client.class);
        if(new PostgreSQLClient().createClient(client)){
            return Response.status(Response.Status.OK).entity(new Gson().toJson(client)).build();
        }
        else{
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("clientPostgres")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientPostgres(@QueryParam("accountNumber") int accountNumber) {
        LOG.log(Level.INFO, "RESTResource.getClientPostgres called");
        Client client = new PostgreSQLClient().readClient(accountNumber);
        return Response.status(Response.Status.OK).entity(new Gson().toJson(client)).build();
    }

    @Path("clientPostgres")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteClientPostgres(@QueryParam("accountNumber") int accountNumber) {
        LOG.log(Level.INFO, "RESTResource.deleteClientPostgres called");
        if( new PostgreSQLClient().deleteClient(accountNumber)){
            return Response.status(Response.Status.OK).entity(new Gson().toJson("{accountNumber:"+accountNumber+"}")).build();
        }
        else{
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("clientPostgres")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateClientPostgres(InputStream inputStream) {
        LOG.log(Level.INFO, "RESTResource.updateClientPostgres called");
        String jsonString = fromInputStreamToString(inputStream);
        Gson gson = new Gson();
        Client client = gson.fromJson(jsonString, Client.class);
        if(new PostgreSQLClient().updateClient(client.getAccountNumber(), client.getBalance())){
            return Response.status(Response.Status.OK).entity(new Gson().toJson(client)).build();
        }
        else{
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("hello")
    @GET
    @Produces("text/html")
    public Response hello(){
        LOG.log(Level.INFO, "RESTResource.hello called");
        String output = "<h1>Hello SDCN!<h1>";
        return Response.status(200).entity(output).build();
    }
}