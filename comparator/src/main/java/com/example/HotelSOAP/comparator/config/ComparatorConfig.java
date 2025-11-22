package com.example.HotelSOAP.comparator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class ComparatorConfig {

  @Bean(name = "a1Client")
  public com.example.HotelSOAP.comparator.clients.a1.AgencyService a1Client()
          throws MalformedURLException {

    com.example.HotelSOAP.comparator.clients.a1.AgencyServiceImplService factory =
            new com.example.HotelSOAP.comparator.clients.a1.AgencyServiceImplService(
                    new URL("http://localhost:9090/agencyservice?wsdl")
            );

    return factory.getAgencyServiceImplPort();
  }

  @Bean(name = "a2Client")
  public com.example.HotelSOAP.comparator.clients.a2.AgencyService a2Client()
          throws MalformedURLException {

    com.example.HotelSOAP.comparator.clients.a2.AgencyServiceImplService factory =
            new com.example.HotelSOAP.comparator.clients.a2.AgencyServiceImplService(
                    new URL("http://localhost:9091/agencyservice?wsdl")
            );

    return factory.getAgencyServiceImplPort();
  }
}