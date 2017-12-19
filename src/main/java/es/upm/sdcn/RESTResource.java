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
    public Response createClient(InputStream inputStream) {
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

    @Path("hello")
    @GET
    @Produces("text/html")
    public Response hello(){
        LOG.log(Level.INFO, "RESTResource.hello called");
        String output = "<h1>Hello SDCN!<h1>";
        return Response.status(200).entity(output).build();
    }
}