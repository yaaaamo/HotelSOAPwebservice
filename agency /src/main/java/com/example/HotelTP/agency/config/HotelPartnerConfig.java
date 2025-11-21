package com.example.HotelTP.agency.config;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Configuration pour initialiser les hôtels partenaires
 */
@Configuration
public class HotelPartnerConfig {

  @Bean(name = "h1Client")
  public com.example.HotelTP.agency.clients.h1.HotelService h1Client()
          throws MalformedURLException {

    com.example.HotelTP.agency.clients.h1.HotelServiceImplService factory =
            new com.example.HotelTP.agency.clients.h1.HotelServiceImplService(
                    new URL("http://localhost:8080/hotelservice?wsdl")
            );

    return factory.getHotelServiceImplPort();
  }


  @Bean(name = "h2Client")
  public com.example.HotelTP.agency.clients.h2.HotelService h2Client()
          throws MalformedURLException {

    com.example.HotelTP.agency.clients.h2.HotelServiceImplService factory =
            new com.example.HotelTP.agency.clients.h2.HotelServiceImplService(
                    new URL("http://localhost:8083/hotelservice?wsdl")
            );

    return factory.getHotelServiceImplPort();
  }
}