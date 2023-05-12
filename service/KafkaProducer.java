package com.vodafone.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vodafone.models.CountriesResponse;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Path("/kafka")
public class KafkaProducer {

    @Inject
    @Channel("store-countries-out")
    Emitter<String> countryEmitter;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    MongoService mongoService;

    Logger logger = Logger.getLogger(KafkaProducer.class.getName());

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendCountry(CountriesResponse countriesResponse) throws JsonProcessingException {
        String countriesResponseJson = objectMapper.writeValueAsString(countriesResponse);
        countryEmitter.send(Message.of(countriesResponseJson));
    }

    @GET
    @Path("/{country-name}")
    public List<CountriesResponse> sentCountryName(@PathParam("country-name") String countryName) {

        List<CountriesResponse> countryList = new ArrayList<>();
        CountriesResponse kafkaCountriesResponse = new CountriesResponse();
        Optional<CountriesResponse> optionalCountriesResponse = mongoService.getCachedCountriesResponse(countryName, "countryName", "KafkaCountries");

        if (optionalCountriesResponse.isPresent()) {
            kafkaCountriesResponse = optionalCountriesResponse.get();
        }

        countryList.add(kafkaCountriesResponse);
        return countryList;
    }

}