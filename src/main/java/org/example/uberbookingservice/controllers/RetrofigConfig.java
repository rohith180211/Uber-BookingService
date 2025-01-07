package org.example.uberbookingservice.controllers;

import com.netflix.discovery.EurekaClient;
import okhttp3.OkHttpClient;
import org.example.uberbookingservice.apis.LocationServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration

public class RetrofigConfig {
    @Autowired
    private EurekaClient eurekaClient;
    private String getServiceUrl(String serviceName) {
         return eurekaClient.getNextServerFromEureka(serviceName,false).getHomePageUrl();
    }
    @Bean
    public LocationServiceApi locationServiceApi() {
        return new Retrofit.Builder().baseUrl(getServiceUrl("UBERPROJECTLOCATIONSERVICE")).addConverterFactory(GsonConverterFactory.create()).client(new OkHttpClient.Builder().build()).build().create(LocationServiceApi.class);
    }
}
