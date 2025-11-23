package com.example.HotelSOAP.server.publisher;
import com.example.HotelSOAP.server.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import javax.xml.ws.Endpoint;
@Configuration
public class HotelServicePublisher implements CommandLineRunner {

  @Autowired
  private HotelService hotelService;

  @Override
  public void run(String... args) throws Exception {
    //default 8080
    String wsPort = System.getProperty("ws.port", "8080");
    String serviceUri = "http://localhost:" + wsPort + "/hotelservice";

    Endpoint.publish(serviceUri, hotelService);
    System.err.println("Web Service successfully published at: " + serviceUri);
    System.err.println("Server ready!");
  }
}
