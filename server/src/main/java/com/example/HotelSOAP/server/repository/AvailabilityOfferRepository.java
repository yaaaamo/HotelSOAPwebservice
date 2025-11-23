package com.example.HotelSOAP.server.repository;

import com.example.HotelSOAP.server.model.AvailabilityOffer;
import com.example.HotelSOAP.server.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AvailabilityOfferRepository extends JpaRepository<AvailabilityOffer, Long> {

  Optional<AvailabilityOffer> findByOfferId(String offerId);
  List<AvailabilityOffer> findByRoom(Room room);
}

