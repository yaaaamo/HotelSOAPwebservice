package com.example.HotelSOAP.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.example.HotelSOAP.server.model",
        "com.example.HotelSOAP.server.service",
        "com.example.HotelSOAP.server.publisher",
        "com.example.HotelSOAP.server.repository",
        "com.example.HotelSOAP.server.data"

})
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

}
