package es.upm.sdcn;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/")
public class RESTfulHelloWorld
{
    @GET
    @Produces("text/html")
    public String defaultResponse() {
        return "Hello Rest Api";
    }

    @Path("hello")
    @GET
    @Produces("text/html")
    public Response hello()
    {
        String output = "<h1>Hello SDCN!<h1>";
        return Response.status(200).entity(output).build();
    }
    @Path("bye")
    @GET
    @Produces("text/html")
    public Response bye()
    {
        String output = "<h1>Bye SDCN!<h1>";
        return Response.status(200).entity(output).build();
    }
}