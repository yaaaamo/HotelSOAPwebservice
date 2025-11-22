package com.example.HotelTP.agency.service;
import com.example.HotelTP.agency.model.AvailabilityOffer;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(serviceName = "AgencyService")
public interface AgencyService {

  @WebMethod
  List<AvailabilityOffer> searchAvailability(
          @WebParam(name = "city") String city,
          @WebParam(name = "startDate") String startDate,
          @WebParam(name = "endDate") String endDate,
          @WebParam(name = "minPrice") Double minPrice,
          @WebParam(name = "maxPrice") Double maxPrice,
          @WebParam(name = "stars") Integer stars,
          @WebParam(name = "persons") int persons
  );


  @WebMethod
  String makeReservation(
          @WebParam(name = "offerId") String offerId,
          @WebParam(name = "clientName") String clientName,
          @WebParam(name = "clientEmail") String clientEmail,
          @WebParam(name = "clientPhone") String clientPhone
  );
}