package com.example.HotelSOAP.server.service;

import com.example.HotelSOAP.server.model.AvailabilityOffer;
import com.example.HotelSOAP.server.model.Client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService
public interface HotelService {

  @WebMethod
  List<AvailabilityOffer> checkAvailability(
          @WebParam(name = "agencyId") String agencyId,
          @WebParam(name = "password") String password,
          @WebParam(name = "startDate") String startDate,
          @WebParam(name = "endDate") String endDate,
          @WebParam(name = "persons") int persons
  );

  @WebMethod
  String book(
          @WebParam(name = "agencyId") String agencyId,
          @WebParam(name = "password") String password,
          @WebParam(name = "offerId") String offerId,
          @WebParam(name = "client") Client client
  );
}