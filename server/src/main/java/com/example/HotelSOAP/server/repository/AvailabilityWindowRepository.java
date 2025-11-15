package com.example.HotelSOAP.server.repository;
import com.example.HotelSOAP.server.model.AvailabilityWindow;
import com.example.HotelSOAP.server.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilityWindowRepository
        extends JpaRepository<AvailabilityWindow, Long> {

  List<AvailabilityWindow> findByRoom(Room room);
}

