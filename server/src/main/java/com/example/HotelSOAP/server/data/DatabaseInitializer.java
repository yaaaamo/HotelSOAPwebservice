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
                                 AvailabilityWindowRepository winRepo) {
    return args -> {


      String hotelId   = System.getProperty("hotel.id",   "H1");
      String hotelName = System.getProperty("hotel.name", "Default Hotel");


      Address adr = new Address(
              "France",
              "Montpellier",
              "Rue de la Loge",
              "42",
              "Écusson",
              43.6108,
              3.8767
      );


      Hotel hotel = new Hotel(hotelId, hotelName, 4, adr);
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

      logger.info("✅ H2 initialized with hotel {} ({}) , rooms and availability",
              hotelName, hotelId);
    };
  }

  // Spring-managed SOAP service (needed for @Autowired in impl)
  @Bean
  public HotelService hotelService() {
    return new HotelServiceImpl();
  }
}
