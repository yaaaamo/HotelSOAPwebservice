package com.example.HotelSOAP.server.repository;
import com.example.HotelSOAP.server.model.Agency;
import com.example.HotelSOAP.server.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, String> {


  Optional<Agency>findByAgencyIdAndPassword(String agencyId, String password);


  List<Agency>findByHotel(Hotel hotel);

}