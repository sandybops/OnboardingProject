package com.vodafone.resources;

import com.vodafone.service.CountriesService;
import com.vodafone.client.RestCountriesClient;
import com.vodafone.models.CountriesResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class CountriesResource {

    @Inject
    @RestClient
    RestCountriesClient countriesClient;
    @Inject
    CountriesService service;

    @GET
    @Path("/countries")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCountries() {
        return countriesClient.getAllCountries();
    }

    @GET
    @Path("/capital/{capital-name}")
    @Produces(MediaType.APPLICATION_JSON)
    public CountriesResponse getCountryByCapital(@PathParam("capital-name") String capitalName) {
        return service.getCountriesResponse(capitalName, "capital");
    }

    @GET
    @Path("/country")
    @Produces(MediaType.APPLICATION_JSON)
    public CountriesResponse getCountryByName(@QueryParam("country-name") String countryName) {
        return service.getCountriesResponse(countryName, "countryName");
    }

}
