package com.vodafone.service;

import com.vodafone.client.RestCountriesClient;
import com.vodafone.models.CountriesResponse;
import com.vodafone.models.Country;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.logging.Logger;


@ApplicationScoped
public class CountriesService {

    Logger logger = Logger.getLogger(CountriesService.class.getName());
    private RestCountriesClient restClient;

    @Inject
    MongoService mongoService;

    public CountriesService(@RestClient RestCountriesClient restClient) {
        this.restClient = restClient;
    }

    public CountriesResponse getCountriesResponse(String param, String paramType) {

        CountriesResponse cachedCountriesResponse;
        Optional<CountriesResponse> optionalCountriesResponse = mongoService.getCachedCountriesResponse(param, paramType, "CountriesCache");
        if (optionalCountriesResponse.isPresent()) {
            logger.info("fetched from cache");
            cachedCountriesResponse = optionalCountriesResponse.get();
            return cachedCountriesResponse;
        } else {
            List<Country> countryList;
            CountriesResponse countriesResponse = new CountriesResponse();

            if (paramType.equals("capital")) countryList = restClient.getCountryByCapital(param);
            else countryList = restClient.getCountryByName(param);

            countryList.stream().filter(Objects::nonNull).findFirst().ifPresent(country -> {
                countriesResponse.setCountryName(country.getCountryName());
                countriesResponse.setCountryCode(country.getCountryCode());
                countriesResponse.setCapital(country.getCapital());
                countriesResponse.setContinent(country.getContinent());
                country.getOfficialLanguage().stream().filter(Objects::nonNull).findFirst().ifPresent(languages -> {
                    countriesResponse.setOfficialLanguage(languages.getName());
                });
                country.getCurrencyName().stream().filter(Objects::nonNull).findFirst().ifPresent(currencies -> {
                    countriesResponse.setCurrencyName(currencies.getName());
                });
            });
            mongoService.addToCache(countriesResponse,"CountriesCache");
            logger.info("country fetched from restcountries");
            return countriesResponse;
        }
    }
}
