package com.example.HotelSOAP.server.data;

import com.example.HotelSOAP.server.model.*;
import com.example.HotelSOAP.server.repository.*;
import com.example.HotelSOAP.server.service.HotelService;
import com.example.HotelSOAP.server.service.HotelServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DatabaseInitializer {

  private final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

  @Bean
  CommandLineRunner initDatabase(HotelRepository hotelRepo,
                                 RoomRepository roomRepo,
                                 AvailabilityWindowRepository winRepo,
                                 AgencyRepository agencyRepo) {
    return args -> {

      String hotelId   = System.getProperty("hotel.id",   "H1");
      String hotelName = System.getProperty("hotel.name", "Default Hotel");

      String country   = System.getProperty("hotel.address.country",   "France");
      String city      = System.getProperty("hotel.address.city",      "Montpellier");
      String street    = System.getProperty("hotel.address.street",    "Rue de la Loge");
      String number    = System.getProperty("hotel.address.number",    "42");
      String placeName = System.getProperty("hotel.address.placeName", "Écusson");
      double latitude  = Double.parseDouble(
              System.getProperty("hotel.address.latitude",  "43.6108"));
      double longitude = Double.parseDouble(
              System.getProperty("hotel.address.longitude", "3.8767"));

      Address adr = new Address(
              country,
              city,
              street,
              number,
              placeName,
              latitude,
              longitude
      );

      String imageUrl;

      if (hotelId.equals("H1")) {
        imageUrl = "http://localhost:8081/images/h1.jpg";
      } else if (hotelId.equals("H2")) {
        imageUrl = "http://localhost:8082/images/h2.jpg";
      } else {
        imageUrl = "/images/default.jpg";
      }

      Hotel hotel = new Hotel(hotelId, hotelName, 4, adr, imageUrl);
      hotelRepo.save(hotel);

      Room r1 = new Room("R1", hotel, RoomType.DOUBLE, 2, 120, 5);
      Room r2 = new Room("R2", hotel, RoomType.FAMILY, 4, 180, 2);
      roomRepo.save(r1);
      roomRepo.save(r2);

      winRepo.save(new AvailabilityWindow(
              r1,
              LocalDate.of(2025, 12, 1),
              LocalDate.of(2025, 12, 10),
              5
      ));
      winRepo.save(new AvailabilityWindow(
              r2,
              LocalDate.of(2025, 12, 1),
              LocalDate.of(2025, 12, 5),
              2
      ));

      // Créer des agences partenaires avec différents facteurs de réduction

      Agency agency1 = new Agency(
              "AGENCY1",
              "agency1",
              "pass123",
              "Travel Express",
              0.90,
              hotel
      );

      Agency agency2 = new Agency(
              "AGENCY2",
              "agency2",
              "pass456",
              "Voyage Plus",
              0.85,
              hotel
      );

      agencyRepo.save(agency1);
      agencyRepo.save(agency2);

      logger.info("✅ H2 initialized with hotel {} ({}), rooms, availability, and {} agencies",
              hotelName, hotelId, agencyRepo.count());
    };
  }

  // Spring-managed SOAP service (needed for @Autowired in impl)
  @Bean
  public HotelService hotelService() {
    return new HotelServiceImpl();
  }
}