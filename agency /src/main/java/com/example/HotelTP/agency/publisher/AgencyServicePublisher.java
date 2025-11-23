package com.example.HotelTP.agency.publisher;

import com.example.HotelTP.agency.service.AgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import javax.xml.ws.Endpoint;

@Configuration
public class AgencyServicePublisher implements CommandLineRunner {

  @Autowired
  private AgencyService agencyService;


  @Override
  public void run(String... args) throws Exception {
    String wsPort = System.getProperty("ws.port", "9090");
    String serviceUri = "http://localhost:" + wsPort + "/agencyservice";

    Endpoint.publish(serviceUri, agencyService);
    System.err.println("Web Service successfully published at: " + serviceUri);
    System.err.println("Server ready!");
  }
}
