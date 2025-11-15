package com.example.HotelSOAP.server.publisher;

import com.example.HotelSOAP.server.service.HotelService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

@Configuration
public class HotelServicePublisher implements CommandLineRunner {

  private final HotelService hotelService;

  public HotelServicePublisher(HotelService hotelService) {
    this.hotelService = hotelService;
  }

  @Override
  public void run(String... args) throws Exception {
    // ws.port VM option'dan gelsin, default 8080 olsun
    String wsPort = System.getProperty("ws.port", "8080");
    String serviceUri = "http://localhost:" + wsPort + "/hotelservice";

    Endpoint.publish(serviceUri, hotelService);
    System.err.println("Web Service successfully published at: " + serviceUri);
    System.err.println("Server ready!");
  }
}
