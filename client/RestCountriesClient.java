package com.vodafone.client;

import com.vodafone.models.Country;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@RegisterRestClient
public interface RestCountriesClient {

    @GET
    @Path("/v2/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCountries();

    @GET
    @Path("/v2/capital/{capital}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Country> getCountryByCapital(@PathParam("capital") String capital);

    @GET
    @Path("/v2/name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Country> getCountryByName(@PathParam("name") String name);

}
