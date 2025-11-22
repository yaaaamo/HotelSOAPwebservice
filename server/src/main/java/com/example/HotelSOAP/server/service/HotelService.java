package com.example.HotelSOAP.server.service;

import com.example.HotelSOAP.server.model.AvailabilityOffer;
import com.example.HotelSOAP.server.model.Client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(
        targetNamespace = "http://hotel.example.com/soap"
)
public interface HotelService {

  // Service web 1 : disponibilités par agence
  @WebMethod
  List<AvailabilityOffer> checkAvailability(
          @WebParam(name="token") String token,
          @WebParam(name="start") String startISO,
          @WebParam(name="end") String endISO,
          @WebParam(name="persons") int persons
  );



  // Service web 2 : réservation via agence
  @WebMethod
  String book(
          @WebParam(name="token") String token,
          @WebParam(name="offerId")  String offerId,
          @WebParam(name="mainGuest") Client client
  );

  @WebMethod
  String login(String agencyId, String password);
}
