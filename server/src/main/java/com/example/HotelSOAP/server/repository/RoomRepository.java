package com.example.HotelSOAP.server.repository;


import com.example.HotelSOAP.server.model.Hotel;
import com.example.HotelSOAP.server.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

  List<Room> findByHotel(Hotel hotel);
}
