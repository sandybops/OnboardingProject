package com.vodafone.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.vodafone.models.CountriesResponse;
import org.bson.Document;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class MongoService {

    @PostConstruct
    void init() {
        createIndex();
    }

    Logger logger = Logger.getLogger(CountriesService.class.getName());

    @Inject
    MongoClient mongoClient;

    public Optional<CountriesResponse> getCachedCountriesResponse(String param, String paramType, String collectionName) {

        String capitalFirstLetterParam = capitalizeFirstLetter(param);
        Optional<CountriesResponse> optionalCountriesResponse;
        MongoCollection<CountriesResponse> collection = mongoClient.getDatabase("mydatabase").getCollection(collectionName, CountriesResponse.class);

        logger.info("Executing query: " + "collection.find(eq(\"" + paramType+ "\", \"" + capitalFirstLetterParam + "\")).first()");
        optionalCountriesResponse = Optional.ofNullable(collection.find(eq(paramType, capitalFirstLetterParam)).first());

        if (optionalCountriesResponse.isPresent()) {
            logger.info(capitalFirstLetterParam + " found in collection " + collectionName + ".");
        } else {
            logger.info(capitalFirstLetterParam + " not found in collection " + collectionName + ".");
        }
        return optionalCountriesResponse;
    }

    public void addToCache(CountriesResponse countriesResponse, String collectionName) {
        Document document = new Document()
                .append("countryName", countriesResponse.getCountryName())
                .append("countryCode", countriesResponse.getCountryCode())
                .append("capital", countriesResponse.getCapital())
                .append("continent", countriesResponse.getContinent())
                .append("officialLanguage", countriesResponse.getOfficialLanguage())
                .append("currencyName", countriesResponse.getCurrencyName())
                .append("timeOfEntry", new Date());
        mongoClient.getDatabase("mydatabase").getCollection(collectionName).insertOne(document);
    }

    public void createIndex() {
        IndexOptions indexOptions = new IndexOptions();
        indexOptions.expireAfter(60L, TimeUnit.SECONDS);
        mongoClient.getDatabase("mydatabase").getCollection("CountriesCache")
                .createIndex(new Document("timeOfEntry", 1), new IndexOptions().expireAfter(600L, TimeUnit.SECONDS));
    }

    public static String capitalizeFirstLetter(String str) {
        char[] chars = str.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

}
