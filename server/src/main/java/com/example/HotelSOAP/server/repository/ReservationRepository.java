package com.example.HotelSOAP.server.repository;
import com.example.HotelSOAP.server.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  List<Reservation> findByAgencyIdOrderByCreatedAtDesc(String agencyId);

}

